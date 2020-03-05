package overCatchTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OverCatchTest9 {

	public void constructorThrowsException() {

		try {

			new FileReader("");
		} catch (IOException ex) {

		}
	}
}
