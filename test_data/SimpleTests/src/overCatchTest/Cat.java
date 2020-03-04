package overCatchTest;

public class Cat extends Animal{
	private String behavior;
	private String eat;
	
	public Cat() {
		this.behavior="miow";
		this.eat="cat food";
	}
	
	public String getBehavior() {
		return behavior;
	}
	
	public String getEat() {
		return eat;
	}
	
}
