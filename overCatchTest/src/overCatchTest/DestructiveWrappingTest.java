package overCatchTest;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class DestructiveWrappingTest {
	
	public void A() throws OtherException {
		try {
			throw new FileNotFoundException();
		}catch(FileNotFoundException e) {
			throw new OtherException(e.getMessage());
		}
	}
	
//	if handling both checked and unchecked Exception, only consider the throw inside catch block
	
	public void B() throws FileNotFoundException {
//		must use throws FileNotFoundException because it is checked exception
		try {
			
		}catch(Exception e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}
	
	public void C() {
		try {
//			unchecked exception does not need to be declared in the method declartion with throws
		}catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}

}
