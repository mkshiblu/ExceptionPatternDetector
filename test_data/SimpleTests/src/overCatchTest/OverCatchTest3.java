package overCatchTest;


//unchecked Exception : one class
public class OverCatchTest3 {
	
	public void bar() {
		try {
			foo();
		}catch(RuntimeException e){
			
		}
	}
	
	private void foo() {
		foo1();
	}
	
	private void foo1() {
		throw new java.lang.NullPointerException();
	}

}
