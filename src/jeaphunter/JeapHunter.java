package jeaphunter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.entities.JTryStatement;
import jeaphunter.plugin.PluginConsole;
import jeaphunter.visitors.TryStatementVisitor;
import jeaphunter.visitors.TryVisitor;

/**
 * Class contains various exception anti-pattern detection methods
 */
public class JeapHunter {

	public static PrintStream Console = System.out;

	private JeapHunterProject project;
	private HashSet<TryStatement> projectNestedTryStatements = new HashSet<TryStatement>();
	private List<JTryStatement> tryWithOverCatch = new ArrayList<JTryStatement>();

	PrintStream console = System.out;

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
				// projectNestedTryStatements.addAll(detectNestedTry(sourceFile));
				detectDestructiveWrapping(sourceFile);
				tryWithOverCatch.addAll(detectOverCatch(sourceFile));
			}

			tryWithOverCatch.forEach(x -> System.out.println(x));

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// printNestedTryResults();
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

	public void detectDestructiveWrapping(SourceFile sourceFile) {

	}

	/**
	 * Detect patterns when catch is given but there is no throw for that catch
	 * 
	 * @param compilationUnit
	 */
	public List<JTryStatement> detectOverCatch(SourceFile sourceFile) {
		CompilationUnit cu = sourceFile.getCompilationUnit();
		TryVisitor visitor = new TryVisitor(cu, sourceFile.getFilePath());
		visitor.setMustHaveCatchClause(true);
		cu.accept(visitor);

		List<JTryStatement> rootLevelTryStatements = visitor.getTryStatements();
		AntiPatternDetector ocd = new AntiPatternDetector(rootLevelTryStatements);
		return ocd.detect();
	}

	private void printNestedTryResults() {
		PluginConsole.writeLine("NESTED TRY RESULTS(" + projectNestedTryStatements.size() + " items):\n");

		for (TryStatement nestedTryStatement : projectNestedTryStatements) {
			CompilationUnit compilationUnit = (CompilationUnit) nestedTryStatement.getRoot();
			int lineNumber = compilationUnit.getLineNumber(nestedTryStatement.getStartPosition());
			PluginConsole
					.writeLine(compilationUnit.getTypeRoot().getJavaProject().getProject().getName() + " project: ");
			PluginConsole.writeLine(compilationUnit.getTypeRoot().getElementName() + " at Line:" + lineNumber + "\n");
			// PluginConsole.writeLine(nestedTryStatement.toString() + "\n");
		}
	}
}
