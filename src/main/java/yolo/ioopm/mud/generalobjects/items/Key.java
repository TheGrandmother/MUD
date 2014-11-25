package yolo.ioopm.mud.generalobjects.items;

import yolo.ioopm.mud.generalobjects.Character;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.Item;
import yolo.ioopm.mud.generalobjects.Room;

public class Key extends Item{

	//private final String start_room;
	private final String target_room;
	
	/**
	 * Creates a key going to the target room.
	 * 
	 * @param target_room The room to which the key goes!
	 * @param level The minimum level required to use the key.
	 */
	public Key(String target_room, int level) {
		super("Key to " + target_room, "This key goes  to " + target_room, -1, Type.KEY, true, 1, level);
		//this.start_room = start_room;
		this.target_room = target_room;
	}
	
	/**
	 * Returns the target_room.
	 * @return the target_room
	 */
	public String getTargetRomm(){
		return target_room;
	}
	
	/**
	 * This function test weather or not the key can be used.<p>
	 * 
	 * 
	 * @param user	Who uses the key
	 * @param target What it is being tried to use with
	 * @return True if the use was successful
	 * @throws UseFailedException Thrown if the use failed.
	 * @deprecated This function is silly and from an older implementation of the {@see Item} class and is likley to be removed or changed.
	 */
	public boolean use(Character user, Entity target) throws UseFailedException {
		
		if(!(target instanceof Room)){
			throw new UseFailedException("Keys can only be used with rooms.");
			
		}
		
		if(user.getCs().getLevel() < this.getLevel()){
			throw new UseFailedException("Key requires level " + getLevel() + " but you are only level" + user.getCs().getLevel()+".");
		}
		
		return true;
		
	}

	@Override
	public String inspect() {
		return super.getDescription() + "Requires level: " + super.level;
	}
	
}
