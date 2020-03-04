package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

public class CatchVisitor extends ASTVisitor {

	private final static List<CatchClause> destructiveWrapping = new ArrayList<>();
	@Override
	public boolean visit(CatchClause node) {
		ThrowStatementVisitor throwStatementVisitor = new ThrowStatementVisitor();
		node.getBody().accept(throwStatementVisitor);
		if(throwStatementVisitor.isthrowStatementVisited()) {
			destructiveWrapping.add(node);
		}
		return super.visit(node);
	}
	
	public List<CatchClause> getDestructiveWrapping(){
		return destructiveWrapping;
	}
}
