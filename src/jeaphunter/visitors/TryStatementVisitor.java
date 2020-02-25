package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryStatementVisitor extends ASTVisitor {
	
//	private static List<MethodInvocation> invoked_methods=new ArrayList<>();
	private static HashSet<String> catch_exceptions = new HashSet<>();
	private static HashSet<String> invoked_exceptions = new HashSet<>();
	private HashMap<String,String> invoked_methods=new HashMap<>();
	
	private HashSet<TryStatement> tryStatements = new HashSet<>();
	public HashSet<TryStatement> getTryStatements() {
		return tryStatements;
	}
	@Override
	public boolean visit(TryStatement node) {
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryInvocSwitch");
		node.accept(methodInvocationVisitor);
		invoked_exceptions.addAll(methodInvocationVisitor.getAllException());
		tryStatements.add(node);
		return super.visit(node);
	}
	

	public HashMap<String,String> getInvokedMethods(){
		return invoked_methods;
	}
	
	public void getCatchException(TryStatement node){
		List<CatchClause> catch_clauses_list = node.catchClauses();
		for(CatchClause catch_caluse: catch_clauses_list) {
			System.out.println(catch_caluse.getException().getType().toString());
			catch_exceptions.add(catch_caluse.getException().getType().toString());
		}
	}
	
	public boolean overCatchAntiPattern() {
		if(this.catch_exceptions.equals(this.invoked_exceptions)) {
			return true;
		}else {
			return false;
		}
	}
	
	
}
