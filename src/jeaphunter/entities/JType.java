package jeaphunter.entities;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class JType {
	private ITypeBinding typeBinding;

	public JType(ITypeBinding typeBinding) {
		this.typeBinding = typeBinding;
	}

	@Override
	public String toString() {
		if (typeBinding != null)
			return typeBinding.getQualifiedName();

		return super.toString();
	}
}
