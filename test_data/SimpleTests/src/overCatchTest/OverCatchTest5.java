package overCatchTest;

import java.io.File;

////unchecked Exception : one class without mentioning the excpetion 2
public class OverCatchTest5 {
	
	public void A()
	{
		try{
			B();
		}catch(RuntimeException e) {
			
		}
	}
	
	public void B() {
		
		C();
	}
	
	private void C() {
		File file = new File("filepath");
		file.toPath();
	}
}
