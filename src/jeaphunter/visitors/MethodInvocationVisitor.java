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
		System.out.println("MethodInvocation");
		IMethodBinding methodBinding=node.resolveMethodBinding();

		if(methodBinding!=null ) {
			 for(ITypeBinding exception:methodBinding.getExceptionTypes()) {
//				 System.out.println("methodBinding.getDeclaringClass().toString()"+methodBinding.getDeclaringClass().getDeclaredMethods().toString());
//				 System.out.println("XYZ"+exception.getName().toString());
//				 System.out.println("declaring "+methodBinding.getDeclaringClass().getModule().));
//				 System.out.println("declared reveiveing "+methodBinding.getDeclaredReceiverType());
//				 System.out.println("Typename key"+methodBinding.getDeclaringClass().getKey().toString());
//				 System.out.println("Typedeclaration"+methodBinding.getDeclaringClass().getTypeDeclaration().toString());
//				 System.out.println("get name"+methodBinding.getDeclaringClass().getName().toString());
				 System.out.println("get java element"+methodBinding.getDeclaringClass().getJavaElement().getJavaProject().toString());
//				 System.out.println("get class"+methodBinding.getDeclaringClass().getClass().toString());
			 }
		}else {
			System.out.println("IMethodBinding null");
		}
		return super.visit(node);
	}
	
	public void getSecondLevelInvokedException(MethodInvocation node) {
		List<String> invoked_exceptions_s = new ArrayList<>();
		IMethodBinding methodBinding=node.resolveMethodBinding();
		System.out.println(methodBinding.getMethodDeclaration());
	}
	
	
	
}
