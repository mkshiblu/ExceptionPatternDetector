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
	private String nodename;
	
	public MethodDeclarationVisitor(String nodename) {
		this.nodename=nodename;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		unit_methods.add(node);
		methodCount += 1;

		if(node.getName().toString().contains(nodename)) {
			System.out.println("start");
			System.out.println(node.getName().toString());
			System.out.println(nodename);
			
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
