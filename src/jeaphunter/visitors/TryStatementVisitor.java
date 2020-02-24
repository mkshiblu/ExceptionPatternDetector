package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;


public class TryStatementVisitor extends ASTVisitor {
	
	private static List<MethodInvocation> invoked_methods=new ArrayList<>();
	private static List<String> catch_exceptions = new ArrayList<>();
	
	private HashSet<TryStatement> tryStatements = new HashSet<>();
	public HashSet<TryStatement> getTryStatements() {
		return tryStatements;
	}
	@Override
	public boolean visit(TryStatement node) {
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryInvocSwitch");
		node.accept(methodInvocationVisitor);
		
		tryStatements.add(node);

		return super.visit(node);
	}
	

	public List<MethodInvocation> getInvokedMethods(){
		
		return invoked_methods;
	}
	
	public void getCatchException(TryStatement node){
		List<CatchClause> catch_clauses_list = node.catchClauses();
		for(CatchClause catch_caluse: catch_clauses_list) {
			catch_exceptions.add(catch_caluse.getException().getType().toString());
		}
	
	}
}
