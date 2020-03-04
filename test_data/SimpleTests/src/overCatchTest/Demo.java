package overCatchTest;

public class Demo {

	public static void main(String[] args) {
		Animal cat = new Cat();
		try {
			cat.setAge(0);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		int cat_weight=12;
		try {
			cat.setWeight(cat_weight);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
}
