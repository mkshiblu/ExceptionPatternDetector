package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {
	private static String[] LogMethods = {"log", "info", "warn", "error", "trace", "debug", "fatal"}; // "log statement"
    private static String[] PrintMethods = {"println", "print"}; // "print statement"
	private static String[] DefaultMethods = {"printStackTrace"}; // display statement
	
	private String  TryInvocSwitch;
	private List<String> invoked_exceptions = new ArrayList<>();
	private HashMap<String,String> invoked_methods=new HashMap<>();
	
	private CompilationUnit[] compilationUnits;
	
	
	
	public MethodInvocationVisitor(String TryInvoccSwitch) {
		this.TryInvocSwitch =  TryInvocSwitch;
	}
	
	public boolean visit(MethodInvocation node) {

		List<CompilationUnit> compilationUnitList = new ArrayList<>();
		
		System.out.println("MethodInvocation");
		IMethodBinding methodBinding=node.resolveMethodBinding();
		String nodeName = node.getName().toString();
		String className = methodBinding.getDeclaringClass().getName().toString();
		if(methodBinding!=null && !IsLoggingStatement(nodeName) && !IsDefaultStatement(nodeName) && 
		!IsPrintStatement(nodeName)&& !className.equals("Throwable") ) {
			System.out.println("Class name "+ className);
			System.out.println("Node name "+ nodeName);
			invoked_methods.put(className,nodeName);
			System.out.println(methodBinding.getJavaElement());
			if(methodBinding.getJavaElement() instanceof IMember) {
				IMember member =(IMember) methodBinding.getJavaElement();
				ICompilationUnit cu=member.getCompilationUnit();
				compilationUnitList.add(parse(cu));
				compilationUnits = compilationUnitList.toArray(new CompilationUnit[compilationUnitList.size()]);
				System.out.println("start cu");
				System.out.println(cu.toString());
				System.out.println("get cu");
				System.out.println("print unit");
				getSecondLevelInvokedMethod(compilationUnits,nodeName);
				printUnit();
			}
			
	
			System.out.println(".getDeclaredMethods()"+methodBinding.getDeclaringClass().getDeclaredMethods());
			 for(ITypeBinding exception:methodBinding.getExceptionTypes()) {
//				 System.out.println("Exception ToString" +exception.toString());
				 invoked_exceptions.add(exception.toString());
			 }
		}
		
		return super.visit(node);
	}
	
	public void getSecondLevelInvokedMethod(CompilationUnit[] cus, String nodename){
		System.out.println("start comapre");
		for(CompilationUnit unit: cus) {
			if(unit.toString().contains(nodename))
			{
				MethodDeclarationVisitor MDvisitor = new MethodDeclarationVisitor(nodename);
				unit.accept(MDvisitor);
			}
		}
		
		
	}
	
	
	
	
	
    /// To check whether an invocation is a logging statement
    private static boolean IsLoggingStatement(String statement)
    {
        if (statement == null) return false;
        for (String logmethod : LogMethods)
        {
            if (statement.indexOf(logmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }
    
    /// To check whether an invocation is a default statement
	private static boolean IsDefaultStatement(String statement)
	{
        if (statement == null) return false;
        for (String defaultmethod : DefaultMethods)
        {
            if (statement.indexOf(defaultmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }
	
    /// To check whether an invocation is a print statement
	private static boolean IsPrintStatement(String statement)
	{
        if (statement == null) return false;
        for (String defaultmethod : PrintMethods)
        {
            if (statement.indexOf(defaultmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }


	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS13);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
	
	public void printUnit() {
		System.out.println(compilationUnits.toString());
	}
	
}
