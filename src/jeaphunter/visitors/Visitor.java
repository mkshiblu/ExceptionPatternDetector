package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;

import jeaphunter.entities.JTryStatement;
import jeaphunter.entities.JType;
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
		JTryStatement jTry = new JTryStatement();
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
		List<Type> thrownFromSignature = declartion.thrownExceptionTypes();

		for (Type type : thrownFromSignature) {
			rootTry.addToThrownExceptionTypes(new JType(type));
		}

		// TODO: CHeck if catch clause handles all of these
		if (methodInvocationDepth == MAX_DEPTH_OF_SEARCHING_INSIDE_METHOD_INVOCATIONS)
			return false;

		Visitor visitor = new Visitor(rootTry, methodInvocationDepth + 1);
		declartion.accept(visitor);

		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		rootTry.addToThrowedStatements(node);

		Type exceptionType = null;

		try {
			Expression expression = node.getExpression();
			if (expression instanceof ClassInstanceCreation) {
				ClassInstanceCreation instanceCreation = (ClassInstanceCreation) expression;
				exceptionType = instanceCreation.getType();
			} else if (expression instanceof SimpleName) {
				SimpleName simpleName = ((SimpleName) expression);
				// exceptionType = simpleName.resolveTypeBinding().getTypeDeclaration();
				// TODO: How to get TYpe?
			} else if (expression instanceof MethodInvocation) {
				MethodInvocation methodInvocation = ((MethodInvocation) expression);
				MethodDeclaration declaration = ASTUtil.declarationFromInvocation(methodInvocation);
				if (declaration != null) {
					exceptionType = declaration.getReturnType2();
				}

				// TOOD: Also handle other probable cases
			}
		} catch (Exception ex) {
			System.out.println("Exception determining the type of thrown exception type, ignoring: " + node);
		}

		if (exceptionType == null) {
			System.out.println("Cannot determine the type of thrown exception type, ignoring: " + node);
		} else {
			rootTry.addToThrownExceptionTypes(new JType(exceptionType));
		}
		return false;
	}

	public List<JTryStatement> getTryStatements() {
		return jTryStatements;
	}
}
