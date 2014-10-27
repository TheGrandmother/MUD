package yolo.ioopm.mud.generalobjects;

public abstract class Item extends Entity {
	private String name;
	private String description;
	private int uses;
	private Type type;
	boolean dropable;
	
	
	
	//The implementation needs to specify what class the target need to be.
	abstract void use(Character user, Entity target);
	
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Type getType() {
		return type;
	}
	
	public int getUses() {
		return uses;
	}
	
	public void setUses(int uses) {
		this.uses = uses;
	}
	
	private enum Type{
		CONSUMABLE, USABLE,EQUIPABLE
	}
	
}
