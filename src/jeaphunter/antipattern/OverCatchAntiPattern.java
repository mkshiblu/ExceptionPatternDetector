package jeaphunter.antipattern;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class OverCatchAntiPattern extends ExceptionAntiPattern {

	private ITypeBinding catchException;
	private String reason;
	private String location;

	public OverCatchAntiPattern(ITypeBinding catchException) {
		this.catchException = catchException;
	}
	
	public OverCatchAntiPattern(ITypeBinding catchException, String reason) {
		this(catchException);
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}
}
