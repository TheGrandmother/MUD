package yolo.ioopm.mud.generalobjects.items;

import yolo.ioopm.mud.generalobjects.Character;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.Item;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;

public class Key extends Item{

	private final String start_room;
	private final String target_room;
	
	public Key(String start_room, String target_room, int level) {
		super(start_room+"->"+target_room, "This key goes from " + start_room + " to " + target_room, -1, Type.KEY, true, 1, level);
		this.start_room = start_room;
		this.target_room = target_room;
	}
	
	
	@Override
	public boolean use(Character user, Entity target) throws UseFailedException {
		
		if(!(target instanceof Room)){
			throw new UseFailedException("Keys can only be used with rooms.");
		}
		
		if(user.getCs().getLevel() < this.getLevel()){
			throw new UseFailedException("Key requires level " + getLevel() + " but you are only level" + user.getCs().getLevel()+".");
		}
		
		return true;
		
	}
	
}
