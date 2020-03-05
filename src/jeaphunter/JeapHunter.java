package jeaphunter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.antipattern.OverCatchDetector;
import jeaphunter.entities.JTryStatement;
import jeaphunter.util.Cache;
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
	public static IUserConsole UserConsole;

	private JeapHunterProject project;

	public JeapHunter(JeapHunterProject project) {
		this.project = project;
	}

	/**
	 * Detects all Exception anti-patterns in the project
	 */
	public void detectAllExceptionAntiPatterns() {

		try {
			HashSet<TryStatement> projectNestedTryStatements = new HashSet<TryStatement>();
			List<JTryStatement> tryWithOverCatch = new ArrayList<JTryStatement>();
			HashSet<CatchClause> destructiveWrappingResult = new HashSet<>();
			ICompilationUnit[] sourceFiles = project.getSourceFiles();

			UserConsole.println("Hunting " + project.getName() + " Anti-patterns...");
			UserConsole.println();

			if (sourceFiles.length > 0) {

				// UserConsole.println("Detecting Nested Try...");
				for (ICompilationUnit icompUnit : sourceFiles) {
					projectNestedTryStatements.addAll(detectNestedTry(icompUnit));
				}

				if (projectNestedTryStatements.size() > 0) {
					printNestedTryResults(projectNestedTryStatements);
					UserConsole.println();
				}

				// UserConsole.println("Detecting Destructive Wrapping...");
				for (ICompilationUnit icompUnit : sourceFiles) {
					destructiveWrappingResult.addAll(detectDestructiveWrapping(icompUnit));
				}

				if (destructiveWrappingResult.size() > 0) {
					printDestructiveWrapping(destructiveWrappingResult);
					UserConsole.println();
				}

				// UserConsole.println("Detecting Over Catch...");
				for (ICompilationUnit icompUnit : sourceFiles) {
					detectOverCatch(icompUnit);
				}
				tryWithOverCatch.addAll(OverCatchDetector.getTryWithOverCatch());
				if (tryWithOverCatch.size() > 0) {
					printOverCatchResult(tryWithOverCatch);
				}

				UserConsole.println();
				printSummary(projectNestedTryStatements, tryWithOverCatch, destructiveWrappingResult);
				UserConsole.println();
			} else {
				UserConsole.println("No source files loaded for project " + project.getProject().getName());
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		} finally {
			Cache.clear();
			OverCatchDetector.clear();
		}
	}

	public HashSet<TryStatement> detectNestedTry(ICompilationUnit icompUnit) {
		CompilationUnit compilationUnit = Cache.parse(icompUnit, false);

		TryStatementVisitor compilationUnitTryVisitor = new TryStatementVisitor();
		HashSet<TryStatement> compilationUnitNestedTryStatements = new HashSet<>();
		compilationUnit.accept(compilationUnitTryVisitor);
		for (TryStatement tryStatement : compilationUnitTryVisitor.getTryStatements()) {
			TryStatementVisitor tryStatementTryVisitor = new TryStatementVisitor();

			tryStatement.getBody().accept(tryStatementTryVisitor);

			if (tryStatementTryVisitor.getTryStatements().size() > 0) {
				for (TryStatement nestedTryStatement : tryStatementTryVisitor.getTryStatements()) {
					if (nestedTryStatement.catchClauses() != null && nestedTryStatement.catchClauses().size() > 0) {
						compilationUnitNestedTryStatements.add(tryStatement);
					}
				}
			}
		}
		return compilationUnitNestedTryStatements;
	}

	public HashSet<CatchClause> detectDestructiveWrapping(ICompilationUnit icompUnit) {
		CompilationUnit compilationUnit = Cache.parse(icompUnit, false);
		CatchVisitor catchVisitor = new CatchVisitor();
		compilationUnit.accept(catchVisitor);
		return new HashSet<>(catchVisitor.getDestructiveWrapping());
	}

	/**
	 * Detect patterns when catch is given but there is a sub class throw for that
	 * catch.
	 * 
	 * @param compilationUnit
	 */
	public void detectOverCatch(ICompilationUnit icompUnit) {
		CompilationUnit cu = Cache.getCompilationUnit(icompUnit);

		TryVisitor visitor = new TryVisitor();
		visitor.setMustHaveCatchClause(true);
		cu.accept(visitor);

		List<JTryStatement> rootLevelTryStatements = visitor.getTryStatements();
		OverCatchDetector ocd = new OverCatchDetector(rootLevelTryStatements);
		ocd.detect();
	}

	private void printNestedTryResults(HashSet<TryStatement> projectNestedTryStatements) {
		UserConsole.println("---------------NESTED TRY--------------");
		for (TryStatement nestedTryStatement : projectNestedTryStatements) {
			UserConsole.println(mapCompilationUnitToString((CompilationUnit) nestedTryStatement.getRoot(),
					nestedTryStatement.getStartPosition()));
		}
	}

	private void printDestructiveWrapping(HashSet<CatchClause> destructiveWrappingResult) {
		UserConsole.println("---------------DESTRUCTIVE WRAPPING--------------");
		destructiveWrappingResult.stream().map(this::mapCatchClauseToString).forEach(UserConsole::println);
	}

	private void printOverCatchResult(List<JTryStatement> tryWithOverCatch) {
		UserConsole.println("---------------OVER CATCH--------------");
		for (JTryStatement overCatchTry : tryWithOverCatch) {
			UserConsole.println(mapCompilationUnitToString((CompilationUnit) overCatchTry.getTryStatement().getRoot(),
					overCatchTry.getTryStatement().getStartPosition()));
			overCatchTry.getOverCatches().forEach(overCatch -> UserConsole.println(overCatch.getReason()));
		}
	}

	private String mapCompilationUnitToString(CompilationUnit compilationUnit, int startPosition) {
		StringBuilder sb = new StringBuilder();
		// sb.append(compilationUnit.getTypeRoot().getJavaProject().getProject().getName()).append("
		// project: ");
		sb.append(compilationUnit.getTypeRoot().getElementName()).append(" at Line:");
		sb.append(compilationUnit.getLineNumber(startPosition)).append(" col: ");
		sb.append(compilationUnit.getColumnNumber(startPosition));
		return sb.toString();
	}

	private String mapCatchClauseToString(CatchClause catchClause) {
		StringBuilder sb = new StringBuilder();
		sb.append(mapCompilationUnitToString((CompilationUnit) catchClause.getRoot(), catchClause.getStartPosition()));
		sb.append(System.lineSeparator());
		// sb.append(catchClause.toString());
		return sb.toString();
	}

	private void printSummary(HashSet<TryStatement> projectNestedTryStatements, List<JTryStatement> tryWithOverCatch,
			HashSet<CatchClause> destructiveWrappingResult) {
		UserConsole.println(String.format("---------------%s Summary--------------", project.getName()));
		UserConsole.println("Number of Nested Try: " + projectNestedTryStatements.size());
		UserConsole.println("Number of Destructive Wrapping: " + destructiveWrappingResult.size());
		UserConsole.println("Number of Over Catch: " + tryWithOverCatch.size());
	}
}
