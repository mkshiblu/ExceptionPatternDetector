package jeaphunter.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;

/**
 * An abstraction over Ast try statement
 */
public class JTryStatement {

	private List<CatchClause> catchClauses = new ArrayList<>();
	private List<MethodInvocation> invokedMethods = new ArrayList<>();
	private List<ThrowStatement> throwedStatements = new ArrayList<>();

	/**
	 * Directly or deeply nested (i.e. inside a method which is invoked from this
	 * try) try blocks
	 */
	private List<JTryStatement> nestedTryStatements = new ArrayList<>();

	/**
	 * Holds all the throw statement exceptions excluding the ones not inside of
	 * nested try blocks
	 */
	private List<ITypeBinding> thrownExceptionTypes = new ArrayList<>();

	/**
	 * Holds the binding of all the caught exception in the catch blocks of this try
	 */
	private List<ITypeBinding> catchBlockExceptionTypes = new ArrayList<>();

	private Block body;

	private String sourceFilePath;
	private int startLineInSource;
	private int startColumnInSource;
	private String uniqueId;
	private JTryStatement parentTry;

	public List<CatchClause> getCatchClauses() {
		return catchClauses;
	}

	public void addCatchClause(CatchClause catchClause) {
		ITypeBinding typeBinding = catchClause.getException().getType().resolveBinding();
		if (typeBinding != null) {
			this.catchBlockExceptionTypes.add(typeBinding);
		}
		this.catchClauses.add(catchClause);
	}

	public void addCatchClauses(List<CatchClause> catchClauses) {
		for (CatchClause catchClause : catchClauses) {
			this.addCatchClause(catchClause);
		}
	}

	public List<ITypeBinding> getCatchClauseExceptionTypes() {
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

	public List<JTryStatement> geteNestedTryStatements() {
		return nestedTryStatements;
	}

	public void addToThrownExceptionTypes(ITypeBinding exceptionType) {
		this.thrownExceptionTypes.add(exceptionType);
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
				throw new UnsupportedOperationException("SourceFile Path cannot be null to generate Unique ID");
			}

			uniqueId = sourceFilePath + "_" + startLineInSource + "_" + startColumnInSource;
		}

		return uniqueId;
	}

	public List<ITypeBinding> getThrownExceptionTypes() {
		return thrownExceptionTypes;
	}

	public JTryStatement getParentTry() {
		return parentTry;
	}

	public void setParentTry(JTryStatement parentTry) {
		this.parentTry = parentTry;
	}
}