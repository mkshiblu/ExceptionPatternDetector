package jeaphunter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.plugin.PluginConsole;
import jeaphunter.visitors.MethodDeclarationVisitor;
import jeaphunter.visitors.TryStatementVisitor;

/**
 * Class contains various exception anti-pattern detection methods
 */
public class JeapHunter {

	private JeapHunterProject project;
	public JeapHunter(JeapHunterProject project) {
		this.project = project;
	}
	private HashSet<TryStatement> projectNestedTryStatements = new HashSet<TryStatement>();

	/**
	 * Detects all Exception anti-patterns in the project
	 */
	public void detectAllExceptionAntiPatterns() {

		
		CompilationUnit[] compilationUnits;
		try {
			compilationUnits = project.getAllCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				projectNestedTryStatements.addAll(detectNestedTry(compilationUnit));
				detectDestructiveWrapping(compilationUnit);
//				System.out.println(compilationUnit.getJavaElement().getElementName());
				detectOverCatch(compilationUnit);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		PluginConsole.writeLine("NESTED TRY RESULTS("+projectNestedTryStatements.size()+" items):\n");
		for(TryStatement nestedTryStatement: projectNestedTryStatements) {
			CompilationUnit compilationUnit = (CompilationUnit)nestedTryStatement.getRoot();
			int lineNumber = compilationUnit.getLineNumber(nestedTryStatement.getStartPosition());
			//PluginConsole.writeLine(compilationUnit.getTypeRoot().getJavaProject().getProject().getName()+" project: ");
			PluginConsole.writeLine(compilationUnit.getTypeRoot().getElementName()+" at Line:"+lineNumber+"\n");
			//PluginConsole.writeLine(nestedTryStatement.toString()+"\n");
		}
	}
	
	public HashSet<TryStatement> detectNestedTry(CompilationUnit compilationUnit) {
		TryStatementVisitor compilationUnitTryVisitor = new TryStatementVisitor();
		HashSet<TryStatement> compilationUnitNestedTryStatements = new HashSet<>();
		compilationUnit.accept(compilationUnitTryVisitor);
		for(TryStatement tryStatement : compilationUnitTryVisitor.getTryStatements()) {
			TryStatementVisitor tryStatementTryVisitor = new TryStatementVisitor();
			tryStatement.getBody().accept(tryStatementTryVisitor);
			if(tryStatementTryVisitor.getTryStatements().size() > 0) {
				compilationUnitNestedTryStatements.add(tryStatement);
			}
		}
		return compilationUnitNestedTryStatements;
	}

	public void detectDestructiveWrapping(CompilationUnit compilationUnit) {

	}

	
	public void detectOverCatch(CompilationUnit compilationUnit) {
		System.out.println("Start over Catch 1");
		TryStatementVisitor try_state_visitor=new TryStatementVisitor();
		compilationUnit.accept(try_state_visitor);
		System.out.println(try_state_visitor.overCatchAntiPattern());
		
	}
	
}
