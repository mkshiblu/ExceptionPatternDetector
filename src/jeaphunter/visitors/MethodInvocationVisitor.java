package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

public class MethodInvocationVisitor extends ASTVisitor {
	private static String[] LogMethods = {"log", "info", "warn", "error", "trace", "debug", "fatal"}; // "log statement"
    private static String[] PrintMethods = {"println", "print"}; // "print statement"
	private static String[] DefaultMethods = {"printStackTrace"}; // display statement
	
	private static HashSet<String> all_invoked_exceptions = new HashSet<>();
	private static String  TryInvocSwitch;
	
	private CompilationUnit[] compilationUnits;
	
	
	
	public MethodInvocationVisitor(String TryInvocSwitch) {
		this.TryInvocSwitch = TryInvocSwitch;	
	}
	
	public boolean visit(MethodInvocation node) {
		
		
		if(TryInvocSwitch.equals("TryInvocSwitch")){
			System.out.println("TryInvocSwitch");
			System.out.println(TryInvocSwitch);
			System.out.println("ProcessBinding");
			processCheckedException(node,"second_level");		
			
			
//			Process unchecked Exception
			CallHierarchy callHierarchy = CallHierarchy.getDefault();
			IMember[] members= {method};
			MethodWrapper[] methodWrappers=callHierarchy.getCalleeRoots(members);
			HashSet<IMethod> callees= new HashSet<IMethod>();
			for(MethodWrapper mw: methodWrappers) {
				MethodWrapper[] mw2=mw.getCalls(new NullProgressMonitor());
				HashSet<IMethod> temp.getIMethods(mw2);
				callees.addAll(temp);
				
			}
			
			HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers){
				
			}
			
			
			                                                                                                                                                   
			
			
			
			
			
			
		}else if(TryInvocSwitch.equals("second_level")){
			System.out.println("second_level invoke");
			System.out.println(TryInvocSwitch);
			processCheckedException(node,"third_level");
			
		}else if(TryInvocSwitch.equals("third_level")) {
			System.out.println("third_level_invoke");
			System.out.println(TryInvocSwitch);
			processCheckedException(node,"forth_level");
			
		}else if(TryInvocSwitch.equals("forth_level")) {
			System.out.println("forth_level_invoke");
			System.out.println(TryInvocSwitch);
			processCheckedException(node,"fifth_level");
			
		}else if(TryInvocSwitch.equals("fifth_level")) {
			System.out.println("fifth_level_invoke");
			System.out.println(TryInvocSwitch);
			processCheckedException(node,"six_level");
			
		}
		return super.visit(node);
	}
	
	public void getNextLevelInvokedMethod(CompilationUnit[] cus, String nodename,String level){
		System.out.println("start comapre");
		for(CompilationUnit unit: cus) {
//			System.out.println(unit.toString());
			System.out.println("***************go to method declartion 2");
			MethodDeclarationVisitor MDvisitor = new MethodDeclarationVisitor(nodename,level);
			unit.accept(MDvisitor);
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
	
	public HashSet<String> getAllException() {
		return all_invoked_exceptions;
	}
	
	public void printUnit() {
		System.out.println(compilationUnits.toString());
	}
	
	public void processCheckedException(MethodInvocation node,String level) {
		List<CompilationUnit> compilationUnitList = new ArrayList<>();
		IMethodBinding methodBinding=node.resolveMethodBinding();
		
//	    1. Collect all the exception of the first level binding in a list 
			 for(ITypeBinding exception:methodBinding.getExceptionTypes()) {
//				 System.out.println("Exception ToString" +exception.toString());
				 all_invoked_exceptions.add(exception.toString());
			 }
//		2. Go to the second level binding of the invoked method in try block
			String nodeName = node.getName().toString();
			String className = methodBinding.getDeclaringClass().getName().toString();
			if(methodBinding!=null && !IsLoggingStatement(nodeName) && !IsDefaultStatement(nodeName) && 
			!IsPrintStatement(nodeName)&& !className.equals("Throwable") ) {
//		3. Find the invoked method's original java class and covert the class to CompilationUnit for parsing
				if(methodBinding.getJavaElement() instanceof IMember) {
					IMember member =(IMember) methodBinding.getJavaElement();
					ICompilationUnit cu=member.getCompilationUnit();
					compilationUnitList.add(parse(cu));
					compilationUnits = compilationUnitList.toArray(new CompilationUnit[compilationUnitList.size()]);
//		4. 	Filter the invoked method in the original class and re-find whether the invoked method contains another 
//					invoked methods in the original method block
					getNextLevelInvokedMethod(compilationUnits,nodeName,level);
				}
			}
		}	
}
