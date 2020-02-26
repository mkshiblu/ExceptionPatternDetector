package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import jeaphunter.antipatterns.OverCatchAntiPattern;

public class Visitor extends ASTVisitor {

	private ArrayList<OverCatchAntiPattern> ocs = new ArrayList<>();

	public ArrayList<OverCatchAntiPattern> getOverCatchAntiPatterns() {
		return ocs;
	}

	@Override
	public boolean visit(TryStatement tryStatement) {

		List<CatchClause> catchClauses = tryStatement.catchClauses();

		Block body = tryStatement.getBody();
		body.accept(new ASTVisitor() {

			@Override
			public boolean visit(ThrowStatement node) {
				System.out.println(node);
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				
				declarationFromInvocation(node);
				return super.visit(node);
			}
		});

		return super.visit(tryStatement);
	}

	@Override
	public boolean visit(CatchClause node) {

		// System.out.println(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {

		Expression exp = node.getExpression();

		System.out.println(exp);
		return super.visit(node);
	}
	
	//, this only works when the method is declared in an Eclipse project
	static void declarationFromInvocation(MethodInvocation node) {
		IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
		ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		
		if ( unit == null ) {
		   // not available, external declaration
			return;
		}
		
		ASTParser parser = ASTParser.newParser( AST.JLS13 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT );
		parser.setSource( unit );
		parser.setResolveBindings( true );
		CompilationUnit cu = (CompilationUnit) parser.createAST( null );
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode( binding.getKey() );
	}
}
