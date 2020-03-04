package overCatchTest;

public class NestedTryTest {
	
	public void A() {
		try {
			try {
				System.out.print("x");
			}catch(RuntimeException e) {
				e.printStackTrace();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
