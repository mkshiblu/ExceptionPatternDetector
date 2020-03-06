package jeaphunter.visitors;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.entities.JTryStatement;

public class TryVisitor extends ASTVisitor {

	private List<JTryStatement> jTryStatements = new ArrayList<>();
	/**
	 * Consider try which have catch clause
	 */
	private boolean mustHaveCatchClause;

	@Override
	public boolean visit(TryStatement node) {
		if (mustHaveCatchClause && (node.catchClauses() == null || node.catchClauses().size() == 0))
			return true;

		JTryStatement jTry = new JTryStatement(node);
		//jTry.addCatchClauses(node.catchClauses());
		jTry.setBody(node.getBody());

		ASTNode root = node.getRoot();
		if (root instanceof CompilationUnit) {
			CompilationUnit cunit = (CompilationUnit) root;
			jTry.setSoureFilePath(cunit.getTypeRoot().getElementName());
			int lineNo = cunit.getLineNumber(node.getStartPosition());
			int columnNo = cunit.getColumnNumber(node.getStartPosition());
			jTry.setStartLineInSource(lineNo);
			jTry.setStartColumnInSource(columnNo);
			cunit = null;
		}

		jTryStatements.add(jTry);

		// Skip child block visits
		return false;//
	}

	/**
	 * Returns only the root level try statements
	 */
	public List<JTryStatement> getTryStatements() {
		return jTryStatements;
	}

	/**
	 * Set true to consider only try blocks with catch clause
	 * 
	 * @param enabled
	 */
	public void setMustHaveCatchClause(boolean mustHaveCatchClause) {
		this.mustHaveCatchClause = mustHaveCatchClause;
	}
}
