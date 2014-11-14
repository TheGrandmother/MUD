package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

/**
 * This is the "main" class of the database.
 *
 * @author TheGrandmother
 */
public class World {


	public static HashSet<Pc> players = new HashSet<>();
	private HashSet<Npc>  npcs;
	private HashSet<Room> rooms;
	private HashSet<Item> items;

	boolean admin_exists;

	/**
	 * Creates an empty world...
	 */
	public World() {
		npcs = new HashSet<Npc>();
		rooms = new HashSet<Room>();
		items = new HashSet<Item>();
		admin_exists = false;
	}


	/**
	 * Moves character to the desired room and removes the character from the previous room.
	 *
	 * @param character
	 * @param room
	 * @throws EntityNotUnique
	 */
	public void moveCharacter(Character character, Room room) throws EntityNotUnique {
		if(!assertUnique(character.getName(), room.getNpcs()) || !assertUnique(character.getName(), room.getPlayers())) {
			throw new EntityNotUnique();
		}

		if(character.getClass() == Npc.class) {
			room.getNpcs().add((Npc) character);
			character.getLocation().getNpcs().remove(character);
			character.setLocation(room);
		}
		else {
			room.getPlayers().add((Pc) character);
			character.getLocation().getPlayers().remove(character);
			character.setLocation(room);
		}

	}

	/**
	 * Adds an item to the item set.
	 *
	 * @param item
	 * @throws EntityNotUnique If entry was not unique.
	 */
	public void addItem(Item item) throws EntityNotUnique {
		if(!assertUnique(item.getName(), items)) {
			throw new EntityNotUnique();
		}
		items.add(item);
	}

	/**
	 * Adds a character to the appropriate set. Also ensures that the name is unique regardless of
	 * which type of character.
	 *
	 * @param character
	 * @throws EntityNotUnique
	 */
	public void addCharacter(Character character) throws EntityNotUnique {
		if(!assertUnique(character.getName(), npcs)) {
			throw new EntityNotUnique();
		}
		if(!assertUnique(character.getName(), players)) {
			throw new EntityNotUnique();
		}

		if(character.getClass() == Npc.class) {
			npcs.add((Npc) character);
		}
		else {
			players.add((Pc) character);
		}


	}

	/**
	 * Adds a room to the world
	 *
	 * @param room
	 * @throws EntityNotUnique If the room is not unique
	 */
	public void addRoom(Room room) throws EntityNotUnique {
		if(!assertUnique(room.getName(), rooms)) {
			throw new EntityNotUnique();
		}

		rooms.add(room);

	}

	public Room findRoom(String name) throws EntityNotPresent {
		for(Room e : rooms) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent(name);
	}

	public Item findItem(String name) throws EntityNotPresent {
		for(Item e : items) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent(name);
	}

	public Pc findPc(String name) throws EntityNotPresent {
		for(Pc e : players) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent();
	}

	public Npc findNpc(String name) throws EntityNotPresent {
		for(Npc e : npcs) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent(name);
	}


	/**
	 * Asserts the uniqueness of the name in the given set.
	 * This assert method only compares the name.
	 *
	 * @param name
	 * @param set
	 * @return True if name is unique.
	 */
	public static boolean assertUnique(String name, HashSet<? extends Entity> set) {
		for(Entity e : set) {
			if(e.getName().equals(name)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param entity
	 * @param set
	 * @return Returns true if name exists.
	 */
	public static boolean assertExsistence(String name, HashSet<? extends Entity> set) {
		for(Entity e : set) {
			if(e.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public HashSet<Item> getItems() {
		return items;
	}
	
	public HashSet<Npc> getNpcs() {
		return npcs;
	}
	
	public HashSet<Pc> getPlayers() {
		return players;
	}
	
	public HashSet<Room> getRooms() {
		return rooms;
	}
	
	@SuppressWarnings("serial")
	public class EntityNotUnique extends Exception {
		public EntityNotUnique() {
			super();
		}
	}

	@SuppressWarnings("serial")

	public class EntityNotPresent extends Exception {
		String name;
		public EntityNotPresent() {
			super();
		}
		public EntityNotPresent(String name) {
			super();
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}


}
