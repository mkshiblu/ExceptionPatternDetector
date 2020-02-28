package jeaphunter.entities;

import org.eclipse.jdt.core.dom.Type;

public class JType {
	private Type type;
	
	public JType(Type type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}
