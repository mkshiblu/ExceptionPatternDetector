package overCatchTest;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OverCatchTest6 {

	public void foo() {
		try {

		if (332 / 32 == 3)
			throw new FileNotFoundException();

		if (332 / 32 == 3)
			throw new EOFException();

		if (332 / 32 == 3)
			throw new IOException();
		} catch (FileNotFoundException iox) {

		} catch (IOException iox) {

		} catch (Exception ex) {

		}
		throw new NullPointerException();
		}
}
