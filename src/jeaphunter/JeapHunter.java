package jeaphunter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.antipattern.OverCatchDetector;
import jeaphunter.entities.JTryStatement;
import jeaphunter.visitors.CatchVisitor;
import jeaphunter.visitors.TryStatementVisitor;
import jeaphunter.visitors.TryVisitor;

/**
 * Class contains various exception anti-pattern detection methods
 */
public class JeapHunter {

	/**
	 * The user console must be provided to print messages
	 */
	public static IUserConsole Console;

	private JeapHunterProject project;

	public JeapHunter(JeapHunterProject project) {
		this.project = project;
	}

	/**
	 * Detects all Exception anti-patterns in the project
	 */
	public void detectAllExceptionAntiPatterns() {
		SourceFile[] sourceFiles;
		try {
			HashSet<TryStatement> projectNestedTryStatements = new HashSet<TryStatement>();
			List<JTryStatement> tryWithOverCatch = new ArrayList<JTryStatement>();
			HashSet<CatchClause> destructiveWrappingResult = new HashSet<>();
			sourceFiles = project.getSourceFiles();
			for (SourceFile sourceFile : sourceFiles) {
				projectNestedTryStatements.addAll(detectNestedTry(sourceFile));
				destructiveWrappingResult.addAll(detectDestructiveWrapping(sourceFile));
				//tryWithOverCatch.addAll(detectOverCatch(sourceFile));
			}

			Console.println("---------------Summary--------------");
			Console.println("Number of Nested Try: " + projectNestedTryStatements.size());
			Console.println("Number of Destructive Wrapping: " + destructiveWrappingResult.size());
			Console.println("Number of Over Catch: " + tryWithOverCatch.size());

			printNestedTryResults(projectNestedTryStatements);
			printDestructiveWrapping(destructiveWrappingResult);
			printOverCatchResult(tryWithOverCatch);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	public HashSet<TryStatement> detectNestedTry(SourceFile sourceFile) {
		CompilationUnit compilationUnit = sourceFile.getCompilationUnit();
		TryStatementVisitor compilationUnitTryVisitor = new TryStatementVisitor();
		HashSet<TryStatement> compilationUnitNestedTryStatements = new HashSet<>();
		compilationUnit.accept(compilationUnitTryVisitor);
		for (TryStatement tryStatement : compilationUnitTryVisitor.getTryStatements()) {
			TryStatementVisitor tryStatementTryVisitor = new TryStatementVisitor();
			tryStatement.getBody().accept(tryStatementTryVisitor);
			if (tryStatementTryVisitor.getTryStatements().size() > 0) {
				for(TryStatement nestedTryStatement : tryStatementTryVisitor.getTryStatements()) {
					if(nestedTryStatement.catchClauses() != null && nestedTryStatement.catchClauses().size() > 0) {
						compilationUnitNestedTryStatements.add(tryStatement);
					}
				}
			}
		}
		return compilationUnitNestedTryStatements;
	}

	public HashSet<CatchClause> detectDestructiveWrapping(SourceFile sourceFile) {
		CatchVisitor catchVisitor = new CatchVisitor();
		CompilationUnit compilationUnit = sourceFile.getCompilationUnit();
		compilationUnit.accept(catchVisitor);
		return new HashSet<>(catchVisitor.getDestructiveWrapping());
	}

	/**
	 * Detect patterns when catch is given but there is a sub class throw for that
	 * catch.
	 * 
	 * @param compilationUnit
	 */
	public List<JTryStatement> detectOverCatch(SourceFile sourceFile) {
		CompilationUnit cu = sourceFile.getCompilationUnit();
		TryVisitor visitor = new TryVisitor(cu, sourceFile.getFilePath());
		visitor.setMustHaveCatchClause(true);
		cu.accept(visitor);

		List<JTryStatement> rootLevelTryStatements = visitor.getTryStatements();
		OverCatchDetector ocd = new OverCatchDetector(rootLevelTryStatements);
		return ocd.detect();
	}

	private void printNestedTryResults(HashSet<TryStatement> projectNestedTryStatements) {
		Console.println("---------------NESTED TRY--------------");
		for (TryStatement nestedTryStatement : projectNestedTryStatements) {
			Console.println(mapCompilationUnitToString((CompilationUnit) nestedTryStatement.getRoot(),
					nestedTryStatement.getStartPosition()));
			// Console.println(nestedTryStatement.toString() + "\n");
		}
	}

	private void printOverCatchResult(List<JTryStatement> tryWithOverCatch) {
		Console.println("---------------OVER CATCH--------------");
		for (JTryStatement overCatchTry : tryWithOverCatch) {
			Console.println(mapCompilationUnitToString((CompilationUnit) overCatchTry.getTryStatement().getRoot(),
					overCatchTry.getTryStatement().getStartPosition()));
			overCatchTry.getOverCatches().forEach(overCatch -> Console.println(overCatch.getReason()));
		}
	}

	private void printDestructiveWrapping(HashSet<CatchClause> destructiveWrappingResult) {
		Console.println("---------------DESTRUCTIVE WRAPPING--------------");
		destructiveWrappingResult.stream().map(this::mapCatchClauseToString).forEach(Console::println);
	}

	private String mapCompilationUnitToString(CompilationUnit compilationUnit, int startPosition) {
		StringBuilder sb = new StringBuilder();
		sb.append(compilationUnit.getTypeRoot().getJavaProject().getProject().getName()).append(" project: ");
		sb.append(compilationUnit.getTypeRoot().getElementName()).append(" at Line:");
		sb.append(compilationUnit.getLineNumber(startPosition)).append(" col: ");
		sb.append(compilationUnit.getColumnNumber(startPosition));
		return sb.toString();
	}

	private String mapCatchClauseToString(CatchClause catchClause) {
		StringBuilder sb = new StringBuilder();
		sb.append(mapCompilationUnitToString((CompilationUnit) catchClause.getRoot(), catchClause.getStartPosition()));
		sb.append(System.lineSeparator());
		sb.append(catchClause.toString());
		return sb.toString();
	}
}
