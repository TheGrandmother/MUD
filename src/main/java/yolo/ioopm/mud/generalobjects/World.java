package yolo.ioopm.mud.generalobjects;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This is the "main" class of the database.
 * 
 * As of now the namespace for the entities is global. I.e no two entities can have the same name regardless of type.
 *
 * @author TheGrandmother
 */
public class World {

	/**
	 * This set contains all of the {@link Player}} in the game.<p>
	 * <b>INVARIANTS:</b><p>
	 * All players have unique names.<p>
	 * No player has less than 0 health<p>
	 * If a player is logged that player is present in  {@link Character#location}<p>
	 * A Player is only present in one {@link Room}. 
	 */
	public  HashSet<Player>   players;
	/**
	 *  Not yet implemented. Has no invariant at this point.
	 */
	private HashSet<Npc>  npcs;
	/**
	 * This set contains all of the {@link Room} in the game.<p>
	 * <b>INVARIANTS:</b><p>
	 * All rooms have unique names.<p>
	 * All rooms have at least one {@link Room.Exit}<p>
	 * No two {@link ItemContainer}s contain the same item or has an {@link ItemContainer#amount} of 0 or less.<p>
	 * All {@link Player}s in the room are logged in.
	 * 
	 */
	private HashSet<Room> rooms;
	/**
	 * This set contains all of the {@link Item} in the world.<p>
	 * <b>INVARIANTS:</b><p>
	 * All items have unique names.
	 */
	private HashSet<Item> items;
	/**
	 * 
	 * This list contains all of the {@link Lobby} in the world.<p>
	 * <b>INVARIANT:</b><p>
	 * No two lobbies have the same entry level.<p>
	 * There exists one lobby with entry level 0.
	 * 
	 */
	private ArrayList<Lobby> lobby_list;
	

	boolean admin_exists;

	/**
	 * Creates an empty world...
	 */
	public World() {
		players      = new HashSet<>();
		npcs         = new HashSet<>();
		rooms        = new HashSet<>();
		items        = new HashSet<>();
		lobby_list = new ArrayList<>();
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
			room.getPlayers().add((Player) character);
			character.getLocation().getPlayers().remove(character);
			character.setLocation(room);
		}

	}
	/**
	 * 
	 * attempts to add a lobby to the world.
	 * 
	 * @param room_name The name of the room to be associated with this lobby.
	 * @param level The required level for this lobby.
	 * @throws EntityNotPresent If the room could not be found.
	 * @throws EntityNotUnique If there already exists a lobby with the same level
	 */
	public void addLobby(String room_name, int level) throws EntityNotPresent, EntityNotUnique{
		for(Lobby l : lobby_list){
			if(l.getLevel() == level){
				throw new EntityNotUnique(" There already exists a level " + level + "lobby!");
			}
		}
		lobby_list.add(new Lobby(room_name, level));
	}
	
	public Room getLobby(int level){
		int current_level = 0;
		Room current_room = null;
		for(Lobby l : lobby_list){
			if(level >= l.getLevel() && l.getLevel() >= current_level){
				current_level = l.getLevel();
				current_room = l.getRoom();
			}
		}
		return current_room;
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
			players.add((Player) character);
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
	/**
	 * Searches the world for an item
	 * @param name the name to be searched for.
	 * @return	The item if it exists
	 * @throws EntityNotPresent If no such item exists.
	 */
	public Item findItem(String name) throws EntityNotPresent {
		for(Item e : items) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent(name);
	}

	public Player findPc(String name) throws EntityNotPresent {
		for(Player e : players) {
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
	public static boolean assertExistence(String name, HashSet<? extends Entity> set) {
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

	public HashSet<Player> getPlayers() {
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

		public EntityNotUnique(String message) {
			super(message);
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
	
	/**
	 * A lobby is a room into which players spawn when they login or die.<p>
	 * A player will always be respawned in the lobby with the highest level 
	 * which is lower than or equal to his own level.
	 * 
	 * @author The Grandmother
	 *
	 */
	public class Lobby{
		/**
		 * The required level needed to respawn in this lobby.
		 */
		private final int level;
		/**
		 * Which room is this lobby.
		 */
		private final Room room;
		
		/**
		 * Creates a new lobby.
		 * @param room_name the name of the room
		 * @param level The required level to respawn in this lobby.
		 * @throws EntityNotPresent If there is no such room.
		 */
		public Lobby(String room_name, int level) throws EntityNotPresent {
			this.level = level;
			room = findRoom(room_name);
		}
		
		/**
		 * returns the level of the lobby
		 * @return level The required level of the lobby
		 */
		public int getLevel(){
			return level;
		}
		
		/**
		 * The room to which this lobby points.
		 * @return the room to which this lobby points.
		 */
		public Room getRoom(){
			return room;
		}
		
	}


}
