package yolo.ioopm.mud.generalobjects;

public abstract class Item extends Entity {
	private final String NAME;
	private final String DESCRIPTION;
	/**
	 * -1 uses correspond to unlimited uses.
	 */
	private int uses;
	private Type type;
	private final int size;
	boolean dropable;
	public final int level;


//	/**
//	 * 
//	 * Returns true if use was successful. Although successful does not necessarily mean that
//	 * the desired outcome of the use was achieved just that it was possible to use the action.
//	 * 
//	 * @param user
//	 * @param target
//	 * @return true if use was successful
//	 * @throws UseFailedException 
//	 */
//	//public abstract boolean use(Character user, Entity target) throws UseFailedException;

	public Item(String name, String description, int uses, Type type, Boolean dropable, int size, int level) {
		NAME = name;
		DESCRIPTION = description;
		this.uses = uses;
		this.type = type;
		this.dropable = dropable;
		this.size = size;
		this.level= level;

	}
	
	
	public int getLevel() {
		return level;
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

	public static enum Type {
		KEY, WEAPON, CONSUMABLE, EQUIPABLE, DOCUMENT
	}
	
	@SuppressWarnings("serial")
	public class UseFailedException extends Exception{

		private final String reason;
		
		public UseFailedException(String reason) {
			super();
			this.reason = reason;
		}
		
		public String getReason(){
			return reason;
		}
		
	}


}
