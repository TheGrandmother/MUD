package yolo.ioopm.mud.generalobjects;

public abstract class Item extends Entity {
	private final String name        = null;
	private final String description = null;
	private int  uses;
	private Type type;
	private final int size = 0;
	boolean dropable;


	//The implementation needs to specify what class the target need to be.
	abstract void use(Character user, Entity target);


	public int getSize() {
		return size;
	}


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

	private enum Type {
		CONSUMABLE, USABLE, EQUIPABLE
	}

}
