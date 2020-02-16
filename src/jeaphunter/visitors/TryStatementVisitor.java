package jeaphunter.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryStatementVisitor extends ASTVisitor {

	@Override
	public boolean visit(TryStatement node) {
		// TODO Auto-generated method stub
		System.out.println(node.getStartPosition());
		return super.visit(node);
	}
}
