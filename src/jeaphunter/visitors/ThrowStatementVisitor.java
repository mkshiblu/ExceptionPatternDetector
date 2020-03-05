package jeaphunter.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class ThrowStatementVisitor extends ASTVisitor {
	private final String exceptionFullyQualifiedName;
	private boolean throwStatementVisited = false;

	public ThrowStatementVisitor(String exceptionFullyQualifiedName) {
		this.exceptionFullyQualifiedName = exceptionFullyQualifiedName;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		Expression expression = node.getExpression();
		if (expression instanceof ClassInstanceCreation) {
			throwStatementVisited = true;
			ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
			List arguments = classInstanceCreation.arguments();
			for (Object argument : arguments) {
				if (argument instanceof SimpleName) {
					SimpleName simpleNameArgument = (SimpleName) argument;
					if (exceptionFullyQualifiedName.equals(simpleNameArgument.getFullyQualifiedName())) {
						throwStatementVisited = false;
					}
				}
			}
		}
		return super.visit(node);
	}

	public boolean isthrowStatementVisited() {
		return throwStatementVisited;
	}

}
