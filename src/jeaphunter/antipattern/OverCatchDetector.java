package jeaphunter.antipattern;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		boolean hasOverCatch = false;

		final List<ITypeBinding> sortedCatchExceptions = jtry.getCatchClauseExceptionTypes().stream()
				.sorted(new Comparator<ITypeBinding>() {
					@Override
					public int compare(ITypeBinding c1, ITypeBinding c2) {
						if (ASTUtil.isSubClass(c1, c2))
							return -1;

						if (ASTUtil.isSubClass(c2, c1))
							return 1;

						return 0;
					}
				}).collect(Collectors.toList());

		final Set<ITypeBinding> thrownExceptions = new HashSet<ITypeBinding>();
		thrownExceptions.addAll(jtry.getThrownExceptionTypes());
		thrownExceptions.addAll(jtry.getPropagatedExceptionsFromNestedTryStatemetns());

		Set<ITypeBinding> caughtSubExceptions;
		final StringBuilder subExceptionNames = new StringBuilder(); // For printing

		for (final ITypeBinding catchException : sortedCatchExceptions) {
			subExceptionNames.setLength(0);
			// Find the sub exceptions caught by this catch
			caughtSubExceptions = ASTUtil.getMatchedSubClasses(catchException, thrownExceptions);

			if (caughtSubExceptions.size() > 0) {
				// It's an overcatch
				hasOverCatch = true;

				for (final ITypeBinding caughtSubException : caughtSubExceptions) {
					subExceptionNames.append(caughtSubException.getName());
					subExceptionNames.append(", ");
				}
				// Add it to the list of overcatch
				jtry.addToOverCatches(new OverCatchAntiPattern(catchException,
						catchException.getName() + " is catching " + subExceptionNames.toString()));
			}

			// Remove the matched exceptions from the throw
			thrownExceptions.removeAll(caughtSubExceptions);
			if (thrownExceptions.contains(catchException)) {
				thrownExceptions.remove(catchException);
			}
		}

		return hasOverCatch;
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
