package overCatchTest;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.management.modelmbean.XMLParseException;

public class DestructiveWrappingTest {

	public void A() throws OtherException {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			throw new OtherException(e.getMessage());
		}
	}

//	if handling both checked and unchecked Exception, only consider the throw inside catch block

	public void B() throws FileNotFoundException {
		try {
		} catch (Exception e) {
			throw new FileNotFoundException(e.getMessage());
		}
//		must use throws FileNotFoundException because it is checked exception

	}

	public void C() throws XMLParseException {
		try {
//			unchecked exception does not need to be declared in the method declartion with throws
		} catch (Exception e) {
			throw new XMLParseException(e,e.getMessage());
		}
	}

//	public void D() {
//		try {
//		
//		}catch(FileNotFoundException e){
////			throw FileNotFoundExeption;
//			throw e;
//			throw new FileNotFoundException();
//		}
//		
//	}
}
