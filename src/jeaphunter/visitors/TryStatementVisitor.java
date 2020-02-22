package jeaphunter.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryStatementVisitor extends ASTVisitor {
	
	private HashSet<TryStatement> tryStatements = new HashSet<>();
	public HashSet<TryStatement> getTryStatements() {
		return tryStatements;
	}
	@Override
	public boolean visit(TryStatement node) {
		tryStatements.add(node);
		return super.visit(node);
	}
}
