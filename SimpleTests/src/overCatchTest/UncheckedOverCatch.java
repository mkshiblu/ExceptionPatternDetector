package overCatchTest;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UncheckedOverCatch {
	
	public void foo() {
		
		try {
		bar();
		
		}
		catch(NullPointerException ex) {
			
		}
	}
	
	
	public void bar() {
	//throw new NullPointerException();
		
		
		try {
			
			throw new NullPointerException();
		}catch(NullPointerException ex){
			
		}
	}
}
