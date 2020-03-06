package jeaphunter.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.antipattern.OverCatchAntiPattern;

/**
 * An abstraction over Ast try statement
 */
public class JTryStatement {

	private final List<CatchClause> catchClauses = new ArrayList<>();
	private final List<MethodInvocation> invokedMethods = new ArrayList<>();
	private final List<ThrowStatement> throwedStatements = new ArrayList<>();

	/**
	 * Directly or deeply nested (i.e. inside a method which is invoked from this
	 * try) try blocks
	 */
	private final List<JTryStatement> nestedTryStatements = new ArrayList<>();

	/**
	 * Holds all the throw statement exceptions excluding the ones not inside of
	 * nested try blocks
	 */
	private final Map<String, ITypeBinding> thrownExceptionTypes = new HashMap<>();

	/**
	 * Holds the unhandled exception propagated from inner try
	 */
	private final Map<String, ITypeBinding> propagatedExceptionsFromNestedTryStatements = new HashMap<>();

	/**
	 * Holds the binding of all the caught exception in the catch blocks of this try
	 */
	private final Map<String, ITypeBinding> catchBlockExceptionTypes = new HashMap<>();

	private final Set<OverCatchAntiPattern> overCatches = new HashSet<>();

	private Block body;

	private String sourceFilePath;
	private int startLineInSource;
	private int startColumnInSource;
	private String uniqueId;
	private JTryStatement parentTry;
	private TryStatement tryStatement;

	public JTryStatement(TryStatement tryStatement) {
		this.tryStatement = tryStatement;
	}

	public boolean equals(JTryStatement jtry) {
		// return this.getTryStatement().equals(jtry.getTryStatement());
		return this.hashCode() == jtry.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JTryStatement) {
			return this.equals(((JTryStatement) obj));
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getUniqueId().hashCode();// toString().get //this.getTryStatement()..hashCode();
	}

	public TryStatement getTryStatement() {
		return tryStatement;
	}

	public List<CatchClause> getCatchClauses() {
		return catchClauses;
	}

	public void addCatchClause(CatchClause catchClause) {
		ITypeBinding typeBinding = catchClause.getException().getType().resolveBinding();
		if (typeBinding != null) {
			this.catchBlockExceptionTypes.put(typeBinding.getKey(), typeBinding);
		}
		this.catchClauses.add(catchClause);
	}

	public void addCatchClauses(List<CatchClause> catchClauses) {
		for (CatchClause catchClause : catchClauses) {
			this.addCatchClause(catchClause);
		}
	}

	public Map<String, ITypeBinding> getCatchClauseExceptionTypes() {
		return catchBlockExceptionTypes;
	}

	public List<MethodInvocation> getInvokedMethods() {
		return invokedMethods;
	}

	public void addToInvokedMethods(List<MethodInvocation> invokedMethods) {
		this.invokedMethods.addAll(invokedMethods);
	}

	public void addToInvokedMethods(MethodInvocation invokedMethod) {
		this.invokedMethods.add(invokedMethod);
	}

	public List<ThrowStatement> getThrowedStatements() {
		return throwedStatements;
	}

	public void addToThrowedStatements(List<ThrowStatement> throwedStatements) {
		this.throwedStatements.addAll(throwedStatements);
	}

	public void addToThrowedStatements(ThrowStatement throwStatement) {
		this.throwedStatements.add(throwStatement);
	}

	public void addToNestedTryStatements(List<JTryStatement> nestedTryStatements) {
		this.nestedTryStatements.addAll(nestedTryStatements);
	}

	public void addToNestedTryStatements(JTryStatement nestedTryStatement) {
		this.nestedTryStatements.add(nestedTryStatement);
	}

	public List<JTryStatement> getNestedTryStatements() {
		return nestedTryStatements;
	}

	public void addToThrownExceptionTypes(ITypeBinding exceptionType) {
		this.thrownExceptionTypes.put(exceptionType.getKey(), exceptionType);
	}

	public Block getBody() {
		return body;
	}

	public void setBody(Block body) {
		this.body = body;
	}

	public String getSoureFilePath() {
		return sourceFilePath;
	}

	public void setSoureFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public int getStartLineInSource() {
		return startLineInSource;
	}

	public void setStartLineInSource(int startLineInSource) {
		this.startLineInSource = startLineInSource;
	}

	@Override
	public String toString() {
		return sourceFilePath + " line: " + startLineInSource + " column: " + startColumnInSource;
	}

	public int getStartColumnInSource() {
		return startColumnInSource;
	}

	public void setStartColumnInSource(int startColumnInSource) {
		this.startColumnInSource = startColumnInSource;
	}

	public boolean hasCatchClauses() {
		return this.catchClauses.size() > 0;
	}

	/**
	 * Generates unique id based on sourceifle+row+col
	 */
	public String getUniqueId() {

		if (uniqueId == null) {
			if (sourceFilePath == null) {
				System.out.println("SourceFile Path cannot be null to generate Unique ID");
			}

			uniqueId = sourceFilePath + "_" + startLineInSource + "_" + startColumnInSource;
		}

		return uniqueId;
	}

	public Map<String, ITypeBinding> getThrownExceptionTypes() {
		return thrownExceptionTypes;
	}

	public JTryStatement getParentTry() {
		return parentTry;
	}

	public void setParentTry(JTryStatement parentTry) {
		this.parentTry = parentTry;
	}

	public Map<String, ITypeBinding> getPropagatedExceptionsFromNestedTryStatemetns() {
		return propagatedExceptionsFromNestedTryStatements;
	}

	public void addToPropagatedThrowsFromNestedTry(Set<ITypeBinding> thrownFromInnerTry) {
		for (ITypeBinding binding : thrownFromInnerTry) {
			this.propagatedExceptionsFromNestedTryStatements.put(binding.getKey(), binding);
		}
	}

	public Set<OverCatchAntiPattern> getOverCatches() {
		return overCatches;
	}

	public void addToOverCatches(OverCatchAntiPattern oca) {
		this.overCatches.add(oca);
	}

	public boolean hasOverCatches() {
		return overCatches.size() > 0;
	}
}
