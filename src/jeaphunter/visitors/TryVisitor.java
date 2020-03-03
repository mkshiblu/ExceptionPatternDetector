package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

import jeaphunter.antipattern.OverCatchAntiPattern;
import jeaphunter.entities.JTryStatement;

public class TryVisitor extends ASTVisitor {

	private List<JTryStatement> jTryStatements = new ArrayList<>();

	private CompilationUnit cu;
	private String filePath;

	/**
	 * Consider try which have catch clause
	 */
	private boolean mustHaveCatchClause;

	public TryVisitor() {

	}

	public TryVisitor(String filePath) {
		this.filePath = filePath;
	}

	public TryVisitor(CompilationUnit cu, String filePath) {
		this(filePath);
		this.cu = cu;
	}

	@Override
	public boolean visit(TryStatement node) {
		if (mustHaveCatchClause && (node.catchClauses() == null || node.catchClauses().size() == 0))
			return true;

		JTryStatement jTry = new JTryStatement(node);
		jTry.addCatchClauses(node.catchClauses());
		jTry.setBody(node.getBody());
		jTry.setSoureFilePath(filePath);

		if (cu != null) {
			jTry.setStartLineInSource(cu.getLineNumber(node.getStartPosition()));
			jTry.setStartColumnInSource(cu.getColumnNumber(node.getStartPosition()));
		} else {
			ASTNode root = node.getRoot();
			if (root instanceof CompilationUnit) {
				int lineNo = ((CompilationUnit) root).getLineNumber(node.getStartPosition());
				int columnNo = ((CompilationUnit) root).getColumnNumber(node.getStartPosition());
				jTry.setStartLineInSource(lineNo);
				jTry.setStartColumnInSource(columnNo);
			}
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
