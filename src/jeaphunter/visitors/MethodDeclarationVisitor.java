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

	@Override
	public boolean visit(MethodDeclaration node) {
		unit_methods.add(node);
		methodCount += 1;
		
		return super.visit(node);
	}
	
	public int getMethodCount() {
		
		return methodCount;
	}

	public HashSet<MethodDeclaration> getAllMethods() {
		return unit_methods;
	}
	
	
}
