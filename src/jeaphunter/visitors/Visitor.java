package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;

import jeaphunter.entities.JTryStatement;
import jeaphunter.util.ASTUtil;

public class Visitor extends ASTVisitor {

	public static final int MAX_DEPTH_OF_SEARCHING_INSIDE_METHOD_INVOCATIONS = 5;

	private List<JTryStatement> jTryStatements = new ArrayList<>();
	private int methodInvocationDepth = 1;

	JTryStatement rootTry;

	public Visitor(JTryStatement rootTry) {
		this.rootTry = rootTry;
	}

	public Visitor(JTryStatement parentTry, int methodInvocationDepth) {
		this(parentTry);
		this.methodInvocationDepth = methodInvocationDepth;
	}

	@Override
	public boolean visit(TryStatement node) {

		// Ignore try without catch but consider its body
		if (node.catchClauses() == null || node.catchClauses().size() == 0)
			return true;

		JTryStatement jTry = new JTryStatement(node);

		// Don't process if no catch clauses?

		jTry.setParentTry(rootTry);
		jTry.addCatchClauses(node.catchClauses());
		jTry.setBody(node.getBody());
		jTry.setSoureFilePath(rootTry.getSoureFilePath());

		ASTNode root = node.getRoot();
		if (root instanceof CompilationUnit) {
			int lineNo = ((CompilationUnit) root).getLineNumber(node.getStartPosition());
			int columnNo = ((CompilationUnit) root).getColumnNumber(node.getStartPosition());
			jTry.setStartLineInSource(lineNo);
			jTry.setStartColumnInSource(columnNo);
		}

		jTryStatements.add(jTry);
		rootTry.addToNestedTryStatements(jTry);

		Visitor v = new Visitor(jTry);
		node.getBody().accept(v);

		// Skip child block visits
		return false;//
	}

	@Override
	public boolean visit(MethodInvocation node) {
		rootTry.addToInvokedMethods(node);
		MethodDeclaration declartion = ASTUtil.declarationFromInvocation(node);

		if (declartion != null) {
			List<Type> thrownFromSignature = declartion.thrownExceptionTypes();

			for (Type type : thrownFromSignature) {
				ITypeBinding typeBinding = type.resolveBinding();

				if (typeBinding == null) {
					System.out.println("Cannot resolve binding for thow :" + type + "in " + node.getName());
				} else {
					rootTry.addToThrownExceptionTypes(typeBinding);
				}
			}

			// TODO Check thrown from declaration javadoc if any throws
			Javadoc javadoc = declartion.getJavadoc();
			if (javadoc != null) {
				List<ITypeBinding> thrownTypes = ASTUtil.getThrowableExceptionsFromJavadoc(javadoc);
				for (ITypeBinding typeBinding : thrownTypes) {
					rootTry.addToThrownExceptionTypes(typeBinding);
				}
			}

			if (methodInvocationDepth == MAX_DEPTH_OF_SEARCHING_INSIDE_METHOD_INVOCATIONS)
				return false;

			Visitor visitor = new Visitor(rootTry, methodInvocationDepth + 1);
			declartion.accept(visitor);
		}
		return false;
	}
//
//	@Override
//	public boolean visit(ClassInstanceCreation node) {
//		ITypeBinding binding = node.getType().resolveBinding();
//		return false;
//	}

	@Override
	public boolean visit(ThrowStatement node) {
		ITypeBinding typeBinding = null;
		rootTry.addToThrowedStatements(node);
		try {
			Expression expression = node.getExpression();
			if (expression instanceof ClassInstanceCreation) {
				typeBinding = ((ClassInstanceCreation) expression).resolveTypeBinding();
			} else if (expression instanceof SimpleName) {
				SimpleName simpleName = ((SimpleName) expression);
				typeBinding = simpleName.resolveTypeBinding();
			} else if (expression instanceof MethodInvocation) {
				MethodInvocation methodInvocation = ((MethodInvocation) expression);
				MethodDeclaration declaration = ASTUtil.declarationFromInvocation(methodInvocation);
				if (declaration != null) {
					// get return type from the expression
					typeBinding = declaration.getReturnType2().resolveBinding();
				}
				// TOOD: Also handle other probable cases
			}
		} catch (Exception ex) {
			System.out.println("Exception determining the type of thrown exception type, ignoring: " + node);
		}

		if (typeBinding == null) {
			System.out.println("Cannot determine the type of thrown exception type, ignoring: " + node);
		} else {
			rootTry.addToThrownExceptionTypes(typeBinding);
		}
		return false;
	}

	public List<JTryStatement> getTryStatements() {
		return jTryStatements;
	}
}
