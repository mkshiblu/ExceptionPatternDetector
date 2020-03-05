package overCatchTest;

public class OverCatchTest8 {

	public void recursive() {
		try {
			if (21 > 3) {
				recursive();
			}
			throw new NullPointerException();
		} catch (Exception ex) {

		}
	}
}
