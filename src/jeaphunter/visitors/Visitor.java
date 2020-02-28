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

public class Visitor extends ASTVisitor {

	private List<JTryStatement> jTryStatements = new ArrayList<>();

	private CompilationUnit cu;
	private String filePath;

	JTryStatement parentTry;

	public Visitor(JTryStatement parentTry) {
		this.parentTry = parentTry;
	}

	@Override
	public boolean visit(TryStatement node) {
		JTryStatement jTry = new JTryStatement();
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
				jTry.setStartColumnInSource(lineNo);
			}
		}

		
		jTryStatements.add(jTry);

		Visitor v = new Visitor(jTry);
		node.getBody().accept(v);
		
		// Skip child block visits
		return false;//
	}

	@Override
	public boolean visit(MethodInvocation node) {
		parentTry.addToInvokedMethods(node);
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		parentTry.addToThrowedStatements(node);
		Expression exp = node.getExpression();
		return false;
	}

	public List<JTryStatement> getTryStatements() {
		return jTryStatements;
	}
}
