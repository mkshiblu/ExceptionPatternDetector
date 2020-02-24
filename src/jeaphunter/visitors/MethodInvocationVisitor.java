package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {
	private String  TryInvocSwitch;
	private static List<String> invoked_exceptions = new ArrayList<>();
	
	
	public MethodInvocationVisitor(String TryInvoccSwitch) {
		this.TryInvocSwitch =  TryInvocSwitch;
	}
	
	public boolean visit(MethodInvocation node) {
		
		IMethodBinding methodBinding=node.resolveMethodBinding();
		if(methodBinding!=null ) {
			 for(ITypeBinding exception:methodBinding.getExceptionTypes()) {
				 invoked_exceptions.add(exception.getName().toString());
			 }
		}
		return super.visit(node);
	}
	
	public void getSecondLevelInvokedException(MethodInvocation node) {
		List<String> invoked_exceptions_s = new ArrayList<>();
		IMethodBinding methodBinding=node.resolveMethodBinding();
		
		
	}
	
}
