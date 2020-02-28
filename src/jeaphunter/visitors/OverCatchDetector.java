package jeaphunter.visitors;

import java.util.ArrayList;
import java.util.List;

import jeaphunter.entities.JTryStatement;

public class OverCatchDetector {

	private List<JTryStatement> tryStatements;

	public OverCatchDetector(List<JTryStatement> tryStatements) {
		this.tryStatements = tryStatements;
	}

	public List<JTryStatement> detect() {
		List<JTryStatement> result = new ArrayList<>();
		
		for (JTryStatement jTry : tryStatements) {
			Visitor visitor = new Visitor(jTry);
			jTry.getBody().accept(visitor);

			result.add(jTry);
		}
		
		return result;
	}
}
