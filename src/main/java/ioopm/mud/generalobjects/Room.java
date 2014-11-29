package ioopm.mud.generalobjects;

import java.util.HashSet;

import ioopm.mud.exceptions.EntityNotPresent;

public class Room extends Entity {

	private final String description;
	
	private final boolean pvp; 

	private HashSet<Exit> exits   = new HashSet<Exit>();
	private HashSet<Player>   players = new HashSet<Player>();
	private HashSet<Npc>  npcs    = new HashSet<Npc>();
	private HashSet<ItemContainer> items   = new HashSet<ItemContainer>();

	/**
	 * Generates a new room. with pvp set to false.
	 *
	 * @param name        Name of this room.
	 * @param description Description of this room.
	 */
	public Room(String name, String description) {
		super(name);
		this.description = description;
		this.pvp = false;
	}
	/**
	 * 
	 * generates a room with pvp.
	 * 
	 * @param name name of the room
	 * @param description a description of the room
	 * @param pvp weather or not pvp is enabled.
	 */
	public Room(String name, String description,boolean pvp) {
		super(name);
		
		this.description = description;
		this.pvp = pvp;
	}

	/**
	 * returns weather or not the room is pvp
	 * @return pvp
	 */
	public boolean isPVP() {
		return pvp;
	}
	
	/**
	 * @return Name of room.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Description of room.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Adds an exit too the room.
	 *
	 * @param r      Room this exit should lead too.
	 * @param locked Whether this exit should be locked or not from the start.
	 * @return True if the exit was successfully added.
	 */
	public boolean addExit(Room r, boolean locked) {
		return exits.add(new Exit(r, locked));
	}

	/**
	 * Adds a player to the room. Also checks if the player is already in the room
	 * and if the player is logged in.
	 *
	 * @param player Player to add.
	 * @return True if player was successfully added. It returns false if the player was already in the room or he was not logged in.
	 */
	//TODO this method is fucking suicidal. I'll need to refactor it to use exceptions instead of dumb retro booleans.
	public boolean addPlayer(Player player) {
		if(World.assertUnique(player.getName(), players) && player.isLoggedIn()){
			return players.add(player);
		}else{
			return false;
		}

		
	}

	/**
	 * Checks if the player is present in the room.
	 * @param player The player to be checked.
	 * @return true if the player is in the room otherwise false.
	 */
	public boolean playerPresent(Player player){
		for(Player p : players){
			if(p.getName().equals(player.getName())){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds an NPC to the room.
	 *
	 * @param n The NPC to add.
	 * @return True if NPC was successfully added.
	 */
	public boolean addNPC(Npc n) {
		for (Player npc : players) {
			if(npc.getName().equals(n.getName())){
				return false;
			}
		}
		return npcs.add(n);
	}

	/**
	 * Adds an item to the room. Either increments the amount or creates a new {@link ItemContainer}
	 *
	 * @param i The item to add.
	 * @return True if the item was successfully added.
	 */
	public boolean addItem(Item i) {
		for (ItemContainer container : items) {
			if(container.getName().equals(i.getName())){
				container.setAmount(container.getAmount()+1);
				return true;
			}
		}
		items.add(new ItemContainer(i));
		return true;
	}
	
	/**
	 * Adds several items (many of the same item). Creates a new {@link ItemContainer} if the item is not already in the room or just adds the amount.
	 * 
	 * @param i
	 * @param amount
	 * @return
	 */
	public boolean addItem(Item i, int amount) {
		for (ItemContainer container : items) {
			if(container.getName().equals(i.getName())){
				container.setAmount(container.getAmount()+amount);
				return true;
			}
		}
		
		items.add(new ItemContainer(i));
		for (ItemContainer container : items) {
			if(container.getName().equals(i.getName())){
				container.setAmount(amount);
				return true;
			}
		}
		return true;
	}
	
	
	/**
	 * tries to remove the specified item. May remove the {@link ItemContainer} or just decrease the amount.
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
	 * Tries to remove the entire itemcontainer.
	 * 
	 * @param i The item to be removed
	 * @throws EntityNotPresent If the item is not in the room
	 */
	public void removeItemCompletley(Item i) throws EntityNotPresent{
		for(ItemContainer ic : items){
			if(ic.getName().equals(i.getName())){
				items.remove(ic);
				return;
			}
		}
		throw new EntityNotPresent("Tried to remove nonexisting item");
	}
	
	/**
	 * Gets exit to specific room
	 * @param name The name of the room which you want.
	 * @return null if no exit to room exists.
	 * @throws EntityNotPresent
	 */
	public Exit getExit(String name){
		for (Exit door : exits) {
			if(door.getNameOfOtherside().equals(name)){
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
		for(Exit d : exits) {
			names[i++] = d.getNameOfOtherside();
		}

		return names;
	}

	/**
	 * Returns the available exits.
	 * @return the set of exits
	 */
	public HashSet<Exit> getExits() {
		return exits;
	}
	
	/**
	 * Returns the set of {@link ItemContainer} in the room
	 * @return
	 */
	public HashSet<ItemContainer> getItems() {
		return items;
	}
	/**
	 * Returns the set of all the {@link Npc}s in the room.
	 * @return
	 */
	public HashSet<Npc> getNpcs() {
		return npcs;
	}

	/**
	 * Returns the set of all of the {@link Player}s in the room.
	 * @return the set of Players.
	 */
	public HashSet<Player> getPlayers() {
		return players;
	}

	/**
	 * Removes the given player from the room.
	 *
	 * @param p The player to remove.
	 * @return True if player was successfully removed.
	 * @throws EntityNotPresent if the player is not in the room
	 */
	public boolean removePlayer(Player p) throws EntityNotPresent {
		if(players.remove(p)){
			return true;
		}else{
			throw new EntityNotPresent("Tried to remove player " + p.getName()+ " but no such player in the room");
		}
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
	 * 
	 * This class specifies how an exit works.
	 * 
	 * @author TheGrandmother
	 *
	 */
	public class Exit {
		private final Room    otherside;
		private       boolean is_locked;

		/**
		 * Constructs the exit-object.
		 *
		 * @param r           Room this exit leads too.
		 * @param lock_status True if exit should be locked, false if unlocked.
		 */
		public Exit(Room r, boolean lock_status) {
			otherside = r;
			is_locked = lock_status;
		}

		/**
		 * Returns current lock value of this exit.
		 *
		 * @return true if locked, else false.
		 */
		public boolean isLocked() {
			return is_locked;
		}

		/**
		 * Locks the exit.
		 */
		public void setLocked() {
			is_locked = true;
		}

		/**
		 * Unlocks the exit.
		 */
		public void setUnlocked() {
			is_locked = false;
		}

		/**
		 * Returns the room on the other side of this exit.
		 *
		 * @return room on other side.
		 */
		public Room getOtherSide() {
			return otherside;
		}

		/**
		 * Retrieves the name of the room on the other side of this exit.
		 *
		 * @return name of room this leads too.
		 */
		public String getNameOfOtherside() {
			return otherside.getName();
		}
	}



	
	
	

}
