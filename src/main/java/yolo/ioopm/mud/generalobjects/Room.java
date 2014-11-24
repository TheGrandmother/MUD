package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;

public class Room extends Entity {

	private final String NAME;
	private final String DESCRIPTION;
	
	private final boolean pvp; 

	private HashSet<Door> exits   = new HashSet<Door>();
	private HashSet<Pc>   players = new HashSet<Pc>();
	private HashSet<Npc>  npcs    = new HashSet<Npc>();
	private HashSet<ItemContainer> items   = new HashSet<ItemContainer>();

	/**
	 * Constructs the Room-object.
	 *
	 * @param name        Name of this room.
	 * @param description Description of this room.
	 */
	public Room(String name, String description) {
		this.NAME = name;
		this.DESCRIPTION = description;
		this.pvp = false;
	}
	
	public Room(String name, String description,boolean pvp) {
		this.NAME = name;
		this.DESCRIPTION = description;
		this.pvp = pvp;
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
		if(World.assertUnique(p.getName(), players)){
			return players.add(p);
		}else{
			return false;
		}

		
	}

	/**
	 * Adds an NPC to the room.
	 *
	 * @param n The NPC to add.
	 * @return True if NPC was successfully added.
	 */
	public boolean addNPC(Npc n) {
		for (Pc npc : players) {
			if(npc.getName().equals(n.getName())){
				return false;
			}
		}
		return npcs.add(n);
	}

	/**
	 * Adds an item to the room.
	 *
	 * @param i The item to add.
	 * @return True if the item was successfully added.
	 */
	public boolean addItem(Item i) {
		for (ItemContainer container : items) {
			if(container.getName().equals(i.getName()) && container.getType()==i.getType()){
				container.setAmount(container.getAmount()+1);
				return true;
			}
		}
		items.add(new ItemContainer(i));
		return true;
	}
	
	
	/**
	 * tries to remove the specified item
	 * @param name
	 * @return returns true if the item could be removed
	 */
	public boolean removeItem(Item i){
		for (ItemContainer container : items) {
			if(container.getName().equals(i.getName())){
				if(container.amount == 1){
					items.remove(container);
				}else{
					container.setAmount(container.getAmount()-1);
				}
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Gets exit to specific room
	 * @param name The name of the room which you want.
	 * @return null if no exit to room exists.
	 * @throws EntityNotPresent
	 */
	public Door getExit(String name){
		for (Door door : exits) {
			if(door.getName().equals(name)){
				return door;
			}
		}
		
		return null;
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

	public HashSet<ItemContainer> getItems() {
		return items;
	}

	public HashSet<Npc> getNpcs() {
		return npcs;
	}

	public HashSet<Pc> getPlayers() {
		return players;
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


	//This needs to be accessible. From the outside.
	public class Door {
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


	public boolean isPVP() {
		return pvp;
	}
	
	
	

}
