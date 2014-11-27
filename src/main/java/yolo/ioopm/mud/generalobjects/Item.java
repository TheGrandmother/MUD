package yolo.ioopm.mud.generalobjects;
/**
 * This is the wrapper class for all of the Items in the game.
 * 
 * @author TheGrandmother
 *
 */
public abstract class Item extends Entity {

	private final String description;
	/**
	 * -1 uses correspond to unlimited uses.
	 */
	private int uses;
	private Type type;
	private final int size;
	private final boolean dropable;
	public final int level;

	/**
	 * 
	 * Creates a new item.
	 * 
	 * @param name The name of the item
	 * @param description	A description of the item
	 * @param uses	How many times the item can be used <b>NOTE:</b> this is likely to be removed in the future
	 * @param type What type of item it is <b>NOTE:</b> this is likely to be removed in the future
	 * @param dropable Weather or not the item can be dropped.
	 * @param size The amount of space the item takes up.
	 * @param level	The minimum level required to use the item.
	 */
	public Item(String name, String description, int uses, Type type, Boolean dropable, int size, int level) {
		super(name);
		this.description = description;
		this.uses = uses;
		this.type = type;
		this.dropable = dropable;
		this.size = size;
		this.level= level;

	}
	/**
	 * This string returns a description of the item.
	 * Which may contain more information than the actual <b>description</b> field.
	 * @return A description of the item.
	 */
	public abstract String inspect();
	
	/**
	 * Returns the required level to use this item.
	 * @return the required level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * The size of the item.
	 * @return the size of the item
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return The name of the object
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the description of the object
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return The type of the object
	 * @deprecated I'll probably use some other system in the future. 
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return How many uses the item has left
	 */
	public int getUses() {
		return uses;
	}

	/**
	 * sets how many uses the item has left.
	 * @param uses how many uses the item has left
	 */
	public void setUses(int uses) {
		this.uses = uses;
	}
	
	/**
	 * Enumerator specifying the different types of objects.
	 * @author TheGrandmother
	 * @deprecated Likely to be removed in favor of a less dumb system.
	 *
	 */
	public static enum Type {
		KEY, WEAPON, CONSUMABLE, EQUIPABLE, DOCUMENT
	}
	
	/**
	 * 
	 * Class for the use failed exception. Used to return a failure message from the {@link use} method.
	 * 
	 * @author TheGrandmother
	 */
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
