package yolo.ioopm.mud.generalobjects;

public abstract class Item extends Entity {
	private final String NAME;
	private final String DESCRIPTION;
	private       int    uses;
	private       Type   type;
	private final int    size;
	boolean dropable;


	//The implementation needs to specify what class the target need to be.
	abstract void use(Character user, Entity target);

	public Item(String name, String description, int uses, Type type, Boolean dropable, int size) {
		NAME = name;
		DESCRIPTION = description;
		this.uses = uses;
		this.type = type;
		this.dropable = dropable;
		this.size = size;

	}

	public int getSize() {
		return size;
	}


	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return DESCRIPTION;
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
