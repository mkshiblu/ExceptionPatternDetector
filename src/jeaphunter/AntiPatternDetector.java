package jeaphunter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;

import jeaphunter.entities.JTryStatement;
import jeaphunter.util.ASTUtil;
import jeaphunter.visitors.Visitor;

public class AntiPatternDetector {

	private List<JTryStatement> tryStatements;

	public AntiPatternDetector(List<JTryStatement> tryStatements) {
		this.tryStatements = tryStatements;
		preporcess();
	}

	public void preporcess() {
		for (JTryStatement jTry : tryStatements) {
			Visitor visitor = new Visitor(jTry);
			jTry.getBody().accept(visitor);
		}
	}

	public List<JTryStatement> detect() {
		List<JTryStatement> result = new ArrayList<>();

		for (JTryStatement jTry : tryStatements) {
			detectOverCatch(jTry);
		}

		return result;
	}

	private void detectOverCatch(JTryStatement jtry) {

		final Set<ITypeBinding> superTypesOfCatch = getTopMostSuperClasses(jtry.getCatchClauseExceptionTypes());
		final Set<ITypeBinding> superTypesOfThrown = getTopMostSuperClasses(jtry.getThrownExceptionTypes());

		System.out.println("Catch");
		superTypesOfCatch.forEach(x -> System.out.println(x.getName()));
		System.out.println("Thrown");
		superTypesOfThrown.forEach(x -> System.out.println(x.getName()));
	}

	private Set<ITypeBinding> getTopMostSuperClasses(final Set<ITypeBinding> thrownExceptions) {
		final Set<ITypeBinding> commonSuperTypes = new HashSet<>();
		for (ITypeBinding typeBinding : thrownExceptions) {
			ITypeBinding matchedSuperClass = ASTUtil.getTopMostSuperClass(typeBinding, thrownExceptions);
			if (matchedSuperClass == null) {
				commonSuperTypes.add(typeBinding);
			} else {
				commonSuperTypes.remove(typeBinding);
				commonSuperTypes.add(matchedSuperClass);
			}
		}

		return commonSuperTypes;
	}
}
