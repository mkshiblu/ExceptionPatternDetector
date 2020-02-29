package jeaphunter;

import java.util.ArrayList;
import java.util.List;

import jeaphunter.entities.JTryStatement;
import jeaphunter.visitors.Visitor;

public class AntiPatternDetector {

	private List<JTryStatement> tryStatements;

	public AntiPatternDetector(List<JTryStatement> tryStatements) {
		this.tryStatements = tryStatements;
	}

	public void preporcess() {
		for (JTryStatement jTry : tryStatements) {
			Visitor visitor = new Visitor(jTry);
			jTry.getBody().accept(visitor);
		}
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
