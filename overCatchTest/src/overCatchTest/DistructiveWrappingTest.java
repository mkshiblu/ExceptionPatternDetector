package overCatchTest;

import java.io.FileNotFoundException;

public class DistructiveWrappingTest {
	
	public void A() throws OtherException {
		try {
		}catch(Exception e) {
			throw new OtherException(e.getMessage());
		}
	}
	
	
	
	public void B() throws FileNotFoundException {
//		must use throws FileNotFoundException because it is checked exception
		try {
			
		}catch(Exception e) {
			throw new FileNotFoundException();
		}
	}
	
	public void C() {
		try {
//			unchecked exception does not need to be declared in the method declartion with throws
		}catch(Exception e) {
			throw new NullPointerException();
		}
	}

}
