package overCatchTest;

import java.io.IOException;


//overcatch of checkedException in one class
public class OverCatchTest1 {
	public class TryException{
		public void test1(){
			try {
				foo();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
		
		private void foo() throws IOException{
			System.out.print("over catachException2");
		}
			
			
}
	
	
	
		

	





