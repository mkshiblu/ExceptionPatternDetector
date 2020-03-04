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
	private HashSet<TryStatement> projectNestedTryStatements = new HashSet<TryStatement>();
	private List<JTryStatement> tryWithOverCatch = new ArrayList<JTryStatement>();

	public JeapHunter(JeapHunterProject project) {
		this.project = project;
	}

	/**
	 * Detects all Exception anti-patterns in the project
	 */
	public void detectAllExceptionAntiPatterns() {
		SourceFile[] sourceFiles;
		try {
			sourceFiles = project.getSourceFiles();
			for (SourceFile sourceFile : sourceFiles) {
				projectNestedTryStatements.addAll(detectNestedTry(sourceFile));
				detectDestructiveWrapping(sourceFile);
				tryWithOverCatch.addAll(detectOverCatch(sourceFile));
			}

			printNestedTryResults();
			printOverCatchResult();

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
				compilationUnitNestedTryStatements.add(tryStatement);
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

	private void printNestedTryResults() {
		Console.println("NESTED TRY RESULTS(" + projectNestedTryStatements.size() + " items):\n");

		for (TryStatement nestedTryStatement : projectNestedTryStatements) {
			CompilationUnit compilationUnit = (CompilationUnit) nestedTryStatement.getRoot();
			int lineNumber = compilationUnit.getLineNumber(nestedTryStatement.getStartPosition());
			Console.println(compilationUnit.getTypeRoot().getJavaProject().getProject().getName() + " project: ");
			Console.println(compilationUnit.getTypeRoot().getElementName() + " at Line:" + lineNumber + "\n");
			Console.println(nestedTryStatement.toString() + "\n");
		}
	}

	private void printOverCatchResult() {
		Console.println("---------------OVER_CATCHES--------------");
		for (JTryStatement overCatchTry : tryWithOverCatch) {
			
			Console.println(overCatchTry);
			overCatchTry.getOverCatches().forEach(overCatch -> Console.println(overCatch.getReason()));
		}
	}
}
