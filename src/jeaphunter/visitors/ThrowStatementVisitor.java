package jeaphunter.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class ThrowStatementVisitor extends ASTVisitor {

	private boolean throwStatementVisited = false;
	
	@Override
	public boolean visit(ThrowStatement node) {
		throwStatementVisited = true;
		return super.visit(node);
	}
	
	public boolean isthrowStatementVisited() {
		return throwStatementVisited;
	}
}
