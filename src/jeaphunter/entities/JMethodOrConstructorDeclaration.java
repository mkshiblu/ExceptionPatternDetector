package jeaphunter.entities;

import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class JMethodOrConstructorDeclaration {
	List<ITypeBinding> thrownExceptionsInSignature;
	List<ITypeBinding> thrownExceptionsInBody;
}
