package overCatchTest;

import java.io.IOException;


//Overcatch checked excpetion of two classes
public class OverCatchTest2 {
	
	public void bar() {
		try {
			Test2help t=new Test2help();
			t.foo();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public class Test2help{
		public Test2help() throws IOException{
			
		}
		
		public void foo() throws IOException {
			throw new IOException();
			
		}
	}
}


