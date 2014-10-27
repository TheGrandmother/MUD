package generalobjects;

public abstract class Item extends Entity {
	private String name;
	private String description;
	private int uses;
	private Type type;
	boolean dropable;
	
	
	
	//The implementation needs to specify what class the target need to be.
	abstract void use(Pc user, Entity target);
	
	
	
	private enum Type{
		CONSUMABLE, USABLE,EQUIPABLE
	}
	
}
