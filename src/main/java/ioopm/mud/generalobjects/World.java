package ioopm.mud.generalobjects;

import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.exceptions.EntityNotUnique;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This is the "main" class of the database.<br>
 * <p>
 * As of now the namespace for the entities is global. I.e no two entities can have the same name regardless of type.<p>
 * <p>
 * Below follows the invariants for the different sets of {@link Entity}s<p>
 * <b>PLAYER INVARIANTS:</b><br>
 * All players have unique names.<br>
 * No player has less than 1 health<br>
 * If a player is logged that player is present in  {@link Character#location}<br>
 * A Player is only present in one {@link Room}.<br>
 * No player gets removed after being added to this set.<p>
 * <p>
 * <b>ROOM INVARIANTS:</b><br>
 * All rooms have unique names.<br>
 * All rooms have at least one {@link Room.Exit}<br>
 * No two {@link ItemContainer}s contain the same item or has an {@link ItemContainer#amount} of 0 or less.<br>
 * All {@link Player}s in the room are logged in.<br>
 * No room gets created or removed at runtime.<br>
 * All items in the {@link ItemContainer}s are present in the {@link Items} set.<br>
 * All exits leads to valid rooms<p>
 * <p>
 * <b>ITEM INVARIANTS:</b><br>
 * All items have unique names.<br>
 * No item has negative size.<br>
 * Weapons have positive damage.<br>
 * Keys have valid rooms.<p>
 * <p>
 * <b>LOBBY INVARIANTS:</b><br>
 * No two lobbies have the same entry level.<br>
 * There exists one lobby with entry level 0.
 *
 * @author TheGrandmother
 */
public class World {

	/**
	 * Specifies whether there exists a administrator in the world. (Currently not used).
	 */
	boolean admin_exists;
	/**
	 * This set contains all of the {@link Player}} in the game.<p>
	 */
	private HashSet<Player> players;
	/**
	 * Not yet implemented. Has no invariant at this point.
	 */
	private HashSet<Npc> npcs;
	/**
	 * This set contains all of the {@link Room} in the game.<p>
	 */
	private HashSet<Room> rooms;
	/**
	 * This set contains all of the {@link Item} in the world.<p>
	 */
	private HashSet<Item> items;
	/**
	 * This list contains all of the {@link Lobby} in the world.<p>
	 */
	private ArrayList<Lobby> lobby_list;

	/**
	 * Creates an empty world...
	 */
	public World() {
		players = new HashSet<>();
		npcs = new HashSet<>();
		rooms = new HashSet<>();
		items = new HashSet<>();
		lobby_list = new ArrayList<>();
		admin_exists = false;
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

	/**
	 * attempts to add a lobby to the world.
	 *
	 * @param room_name The name of the room to be associated with this lobby.
	 * @param level     The required level for this lobby.
	 * @throws EntityNotPresent If the room could not be found.
	 * @throws EntityNotUnique  If there already exists a lobby with the same level
	 */
	public void addLobby(String room_name, int level) throws EntityNotPresent, EntityNotUnique {
		for(Lobby l : lobby_list) {
			if(l.getLevel() == level) {
				throw new EntityNotUnique(" There already exists a level " + level + "lobby!");
			}
		}
		lobby_list.add(new Lobby(room_name, level));
	}

	/**
	 * Finds the lobby with the highest level that is of lower level than the players.
	 *
	 * @param level The level of a player
	 * @return The appropriate lobby for a player of that level
	 */
	public Room getLobby(int level) {
		int current_level = 0;
		Room current_room = null;
		for(Lobby l : lobby_list) {
			if(level >= l.getLevel() && l.getLevel() >= current_level) {
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
		} else {
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

	/**
	 * Attempts to find a room in the world.
	 *
	 * @param name The name of the room to be searched for
	 * @return That room if it it exists
	 * @throws EntityNotPresent If the room cen't be found.
	 */
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
	 *
	 * @param name the name to be searched for.
	 * @throws EntityNotPresent If no such item exists.
	 * @return The item if it exists
	 */
	public Item findItem(String name) throws EntityNotPresent {
		for(Item e : items) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent(name);
	}

	/**
	 * Searches the world for an player
	 *
	 * @param name The name of the player to be found.
	 * @return That player if it exists.
	 * @throws EntityNotPresent If that player does not exist.
	 */
	public Player findPlayer(String name) throws EntityNotPresent {
		for(Player e : players) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent();
	}

	/**
	 * Searches the world for an npc
	 *
	 * @param name The name of the NPCs
	 * @return That nnpc if it exists
	 * @throws EntityNotPresent If that npc does not exist.
	 */
	public Npc findNpc(String name) throws EntityNotPresent {
		for(Npc e : npcs) {
			if(e.getName().equals(name)) {
				return e;
			}
		}
		throw new EntityNotPresent(name);
	}

	/**
	 * Returns the set of items.
	 *
	 * @return the set of items.
	 */
	public HashSet<Item> getItems() {
		return items;
	}

	/**
	 * Returns the set of npcs.
	 *
	 * @return the set of npcs.
	 */
	public HashSet<Npc> getNpcs() {
		return npcs;
	}

	/**
	 * Returns the set of players.
	 *
	 * @return the set of players.
	 */
	public HashSet<Player> getPlayers() {
		return players;
	}

	/**
	 * Returns the set of rooms.
	 *
	 * @return the set of rooms.
	 */
	public HashSet<Room> getRooms() {
		return rooms;
	}


	/**
	 * A lobby is a room into which players spawn when they login or die.<p>
	 * A player will always be respawned in the lobby with the highest level
	 * which is lower than or equal to his own level.
	 *
	 * @author The Grandmother
	 */
	public class Lobby {
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
		 *
		 * @param room_name the name of the room
		 * @param level     The required level to respawn in this lobby.
		 * @throws EntityNotPresent If there is no such room.
		 */
		public Lobby(String room_name, int level) throws EntityNotPresent {
			this.level = level;
			room = findRoom(room_name);
		}

		/**
		 * returns the level of the lobby
		 *
		 * @return level The required level of the lobby
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * The room to which this lobby points.
		 *
		 * @return the room to which this lobby points.
		 */
		public Room getRoom() {
			return room;
		}

	}


}
