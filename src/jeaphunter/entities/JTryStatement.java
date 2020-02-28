package jeaphunter.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;

/**
 * An abstraction over Ast try statement
 */
public class JTryStatement {

	private List<CatchClause> catchClauses = new ArrayList<>();
	private List<MethodInvocation> invokedMethods = new ArrayList<>();
	private List<ThrowStatement> throwedStatements = new ArrayList<>();
	private List<JTryStatement> nestedTryStatements = new ArrayList<>();
	private Block body;

	private String sourceFilePath;
	private int startLineInSource;
	private int startColumnInSource;
	private String uniqueId;

	public List<CatchClause> getCatchClauses() {
		return catchClauses;
	}

	public void addCatchClause(CatchClause catchClause) {
		this.catchClauses.add(catchClause);
	}

	public void addCatchClauses(List<CatchClause> catchClauses) {
		this.catchClauses.addAll(catchClauses);
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
}
