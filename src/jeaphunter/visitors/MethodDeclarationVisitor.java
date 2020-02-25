package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationVisitor extends ASTVisitor {
	
	private HashSet<MethodDeclaration> unit_methods= new HashSet<>();
	
	private int methodCount = 0;
	private static String nodename;
	private static String level;
	
	public MethodDeclarationVisitor(String nodename,String level) {
		this.nodename=nodename;
		this.level=level;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		unit_methods.add(node);
		methodCount += 1;
		if(node.getName().toString().contains(nodename)) {
			System.out.println("MethodDeclarationNode "+node.toString());
			System.out.println("start");
			System.out.println(node.getName().toString());
			System.out.println(this.level);
			MethodInvocationVisitor visitor=new MethodInvocationVisitor(this.level);
			node.getBody().accept(visitor);
		}
		
		return super.visit(node);
	}
	


	
	public int getMethodCount() {
		
		return methodCount;
	}

	public void getSecondLevelInvokeMethod(MethodDeclaration node) {
		MethodInvocationVisitor MIvisitor=new MethodInvocationVisitor("SecondLevel");
		node.accept(MIvisitor);
		
		
	}
	
	
}
