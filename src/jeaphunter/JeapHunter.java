package jeaphunter;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import jeaphunter.visitors.TryStatementVisitor;

/**
 * Class contains various exception anti-pattern detection methods
 */
public class JeapHunter {

	private JeapHunterProject project;

	public JeapHunter(JeapHunterProject project) {
		this.project = project;
	}

	/**
	 * Detects all Exception anti-patterns in the project
	 */
	public void detectAllExceptionAntiPatterns() {

		CompilationUnit[] compilationUnits;
		try {
			compilationUnits = project.getAllCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				// TODO: We probably will end up with one giant visitor to handle
				// all 3 anti patterns at single visit. For now we can call the 3 methods
				// separately
				detectNestedTry(compilationUnit);
				detectDestructiveWrapping(compilationUnit);
				detectOverCatch(compilationUnit);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	public void detectNestedTry(CompilationUnit compilationUnit) {
		compilationUnit.accept(new TryStatementVisitor());
	}

	public void detectDestructiveWrapping(CompilationUnit compilationUnit) {

	}

	public void detectOverCatch(CompilationUnit compilationUnit) {

	}
}
