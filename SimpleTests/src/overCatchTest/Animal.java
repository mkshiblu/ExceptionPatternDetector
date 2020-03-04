package overCatchTest;

public class Animal {
	private int age;
	private int weight;
	
	public Animal() {
		this.age=0;
		this.weight=0;
	}
	
	public void setAge(int age) throws Exception {
		if(age==0) {
			throw new Exception("age cannot be 0 ! ");
		}else {
			this.age=age;
		}
		
	}
	
	
	public void setWeight(int weight) {
		this.weight=weight;
	}
	public int getAge() {
		return age;
	}
	
	public int getWeight() {
		return weight;
	}
}
