package jeaphunter.antipattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;

import jeaphunter.entities.JTryStatement;
import jeaphunter.util.ASTUtil;
import jeaphunter.visitors.Visitor;

public class OverCatchDetector {

	private List<JTryStatement> tryStatements;
	private Set<JTryStatement> tryWithOverCatch = new HashSet<>();

	public OverCatchDetector(List<JTryStatement> tryStatements) {
		this.tryStatements = tryStatements;
		preporcess();
	}

	public List<JTryStatement> detect() {
		for (JTryStatement jTry : tryStatements) {
			detectOverCatch(jTry);
		}
		return new ArrayList<JTryStatement>(tryWithOverCatch);
	}

	private void detectOverCatch(JTryStatement jTry) {

		if (tryWithOverCatch.contains(jTry))
			return;

		for (JTryStatement innerTry : jTry.getNestedTryStatements()) {
			detectOverCatch(innerTry);

			Set<ITypeBinding> innerUnhandled = getUnhandledExceptions(innerTry);
			jTry.addToPropagatedThrowsFromNestedTry(innerUnhandled);
		}

		if (!tryWithOverCatch.contains(jTry) && hasOverCatch(jTry)) {
			tryWithOverCatch.add(jTry);
		}
	}

	/**
	 * Returns all the unhandled exceptions by this try
	 */
	private Set<ITypeBinding> getUnhandledExceptions(final JTryStatement jtry) {
		final Set<ITypeBinding> unhandled = new HashSet<>();
		final Set<ITypeBinding> thrownExceptions = jtry.getThrownExceptionTypes();
		final Set<ITypeBinding> catchExceptions = jtry.getCatchClauseExceptionTypes();

		boolean handledInCatch;

		for (ITypeBinding thrownException : thrownExceptions) {
			handledInCatch = false;

			// Check if the throwed exception was handled by the catch
			for (ITypeBinding catchException : catchExceptions) {
				if (thrownException.isEqualTo(catchException) || ASTUtil.isSubClass(thrownException, catchException)) {
					handledInCatch = true;
					break;
				}
			}

			if (!handledInCatch) {
				unhandled.add(thrownException);
			}
		}

		return unhandled;
	}

	private boolean hasOverCatch(final JTryStatement jtry) {
		final Set<ITypeBinding> catchExceptions = jtry.getCatchClauseExceptionTypes();

		final Set<ITypeBinding> thrownExceptions = jtry.getThrownExceptionTypes();
		thrownExceptions.addAll(jtry.getPropagatedExceptionsFromNestedTryStatemetns());

		for (ITypeBinding catchException : catchExceptions) {
			for (ITypeBinding thrownException : thrownExceptions) {

				if (catchException.isEqualTo(thrownException)) {
					// Exception is handled directly not only with subclasses therefore not an
					// overcatch
					break;
				}

				if (ASTUtil.isSubClass(thrownException, catchException)) {

					// this is an overcatch
					return true;
				}
			}
		}

		return false;
	}

	private Set<ITypeBinding> getTopMostClasses(final Set<ITypeBinding> thrownExceptions) {
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

	private void preporcess() {
		for (JTryStatement jTry : tryStatements) {
			Visitor visitor = new Visitor(jTry);
			jTry.getBody().accept(visitor);
		}
	}

	public Set<JTryStatement> getTryWithOverCatch() {
		return tryWithOverCatch;
	}
}
