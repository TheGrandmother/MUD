package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

public class Room extends Entity {

	private final String NAME;
	private final String DESCRIPTION;

	private HashSet<Door> exits   = new HashSet<Door>();
	private HashSet<Pc>   players = new HashSet<Pc>();
	private HashSet<Npc>  npcs    = new HashSet<Npc>();
	private HashSet<Item> items   = new HashSet<Item>();

	/**
	 * Constructs the Room-object.
	 *
	 * @param name        Name of this room.
	 * @param description Description of this room.
	 */
	public Room(String name, String description) {
		this.NAME = name;
		this.DESCRIPTION = description;
	}

	/**
	 * @return Name of room.
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * @return Description of room.
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Adds an exit too the room.
	 *
	 * @param r      Room this exit should lead too.
	 * @param locked Whether this exit should be locked or not from the start.
	 * @return True if the exit was successfully added.
	 */
	public boolean addExit(Room r, boolean locked) {
		return exits.add(new Door(r, locked));
	}

	/**
	 * Adds a player to the room.
	 *
	 * @param p Player to add.
	 * @return True if player was successfully added.
	 */
	public boolean addPlayer(Pc p) {
		return players.add(p);
	}

	/**
	 * Adds an NPC to the room.
	 *
	 * @param n The NPC to add.
	 * @return True if NPC was successfully added.
	 */
	public boolean addNPC(Npc n) {
		return npcs.add(n);
	}

	/**
	 * Adds an item to the room.
	 *
	 * @param i The item to add.
	 * @return True if the item was successfully added.
	 */
	public boolean addItem(Item i) {
		return items.add(i);
	}

	/**
	 * Returns an array with the names of all rooms that this room is connected too.
	 *
	 * @return Array of names.
	 */
	public String[] getNameOfExits() {
		String[] names = new String[exits.size()];

		int i = 0;
		for(Door d : exits) {
			names[i++] = d.getName();
		}

		return names;
	}

	public HashSet<Door> getExits() {
		return exits;
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
	
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	/**
	 * Removes the given player from the room.
	 *
	 * @param p The player to remove.
	 * @return True if player was successfully removed.
	 */
	public boolean removePlayer(Pc p) {
		return players.remove(p);
	}

	/**
	 * Removes the given NPC from the room.
	 *
	 * @param n The NPC to remove.
	 * @return True if the NPC was successfully removed.
	 */
	public boolean removeNPC(Npc n) {
		return npcs.remove(n);
	}

	/**
	 * Removes the given item from the room.
	 *
	 * @param i The item to remove.
	 * @return True if the item was successfully removed.
	 */
	public boolean removeItem(Item i) {
		return items.remove(i);
	}

	class Door {
		private final Room    otherside;
		private       boolean is_locked;

		/**
		 * Constructs the Door-object.
		 *
		 * @param r           Room this door leads too.
		 * @param lock_status True if door should be locked, false if unlocked.
		 */
		public Door(Room r, boolean lock_status) {
			otherside = r;
			is_locked = lock_status;
		}

		/**
		 * Returns current lock value of this door.
		 *
		 * @return true if locked, else false.
		 */
		public boolean isLocked() {
			return is_locked;
		}

		/**
		 * Locks the door.
		 */
		public void setLocked() {
			is_locked = true;
		}

		/**
		 * Unlocks the door.
		 */
		public void setUnlocked() {
			is_locked = false;
		}

		/**
		 * Returns the room on the other side of this door.
		 *
		 * @return room on other side.
		 */
		public Room getOtherSide() {
			return otherside;
		}

		/**
		 * Retrieves the name of the room on the other side of this door.
		 *
		 * @return name of room this leads too.
		 */
		public String getName() {
			return otherside.getName();
		}
	}

}
