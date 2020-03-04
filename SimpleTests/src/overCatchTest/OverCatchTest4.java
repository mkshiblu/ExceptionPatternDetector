package overCatchTest;


//unchecked Exception : one class without mentioning the exception
public class OverCatchTest4 {
	
	public void A() {
		try {
			B();
		}catch(RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	public void B() {
		C();
	}
	
	public void C() {
		int x=1/0;
	}

}
