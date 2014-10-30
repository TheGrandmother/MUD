package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

/**
 * This is the "main" class of the database.
 *
 * @author TheGrandmother
 */
public class World {


	HashSet<Pc>   players;
	HashSet<Npc>  npcs;
	HashSet<Room> rooms;
	HashSet<Item> items;
	
	
	
	public World(Room default_room){
		players = new HashSet<Pc>();
		npcs = new HashSet<Npc>();
		rooms = new HashSet<Room>();
		items = new HashSet<Item>();
	}

	
	
	public boolean assertUnique(String name,HashSet<? extends Entity> set){
		for (Entity e : set) {
			if(e.getName() == name){return false;}
		}
		return true;
	}
	
	public boolean assertExsistence(Entity entity, HashSet<? extends Entity> set){
		return set.contains(entity);
		
	}
	
	/**
	 * 
	 * Moves character to the desired room and removes the character from the previous room.
	 * 
	 * @param character
	 * @param room
	 * @throws EntityNotUnique 
	 */
	public void moveCharacter(Character character, Room room) throws EntityNotUnique{
		if(!assertUnique(character.getName(), room.getNpcs()) || !assertUnique(character.getName(), room.getPlayers())){
			throw new EntityNotUnique();
		}
		
		if(character.getClass() == Npc.class){
			room.getNpcs().add((Npc)character);
			character.getLocation().getNpcs().remove(character);
			character.setLocation(room);
		}else{
			room.getPlayers().add((Pc)character);
			character.getLocation().getPlayers().remove(character);
			character.setLocation(room);
		}
		
	}
	
	public void addItem(Item item) throws  EntityNotUnique{
		if(!assertUnique(item.getName(), items)){throw new EntityNotUnique();}
		items.add(item);
	}
	
	public void addCharacter(Character character) throws EntityNotUnique{
		if(!assertUnique(character.getName(), npcs)){throw new EntityNotUnique();}
		if(!assertUnique(character.getName(), players)){throw new EntityNotUnique();}
		
		if(character.getClass() == Npc.class){
			npcs.add((Npc)character);
		}else{
			players.add((Pc)character);
		}
		
		
	}
	
	public void addRoom(Room room) throws EntityNotUnique{
		if(!assertUnique(room.getName(), rooms)){throw new EntityNotUnique();}
		
		rooms.add(room);
		
	}
	
	class EntityNotUnique extends Exception{
		public EntityNotUnique(){
			super();
		}
	}
	
	class EntityNotPresent extends Exception{
		public EntityNotPresent(){
			super();
		}
	}

	

}
