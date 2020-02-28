package jeaphunter.visitors;

import java.util.List;

import jeaphunter.entities.JTryStatement;

public class OverCatchDetector {

	private List<JTryStatement> tryStatements;

	public OverCatchDetector(List<JTryStatement> tryStatements) {
		this.tryStatements = tryStatements;
	}

	public void detect() {
		for (JTryStatement jTry : tryStatements) {

			// Skip if no catch clause
			if (!jTry.hasCatchClauses())
				continue;

			Visitor visitor = new Visitor(jTry);
			jTry.getBody().accept(visitor);
		}
	}
}
