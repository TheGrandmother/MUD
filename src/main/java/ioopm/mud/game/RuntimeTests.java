package ioopm.mud.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.ItemContainer;
import ioopm.mud.generalobjects.World;
import ioopm.mud.generalobjects.Entity;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.Room;


/**
 *This class is used to perform invariant checks at runtime.
 * 
 * @author TheGrandmother
 */
public class RuntimeTests {

	public RuntimeTests() {
	
	}
	
	
	public String[] checkRoomInvariant(World world, Boolean strict) throws UnrecoverableInvariantViolation, InvariantViolation{
		ArrayList<String> report = new ArrayList<>();
		
		checkNameCollisions(world.getRooms());
		/*
		 * All rooms have unique names.<p>
		 * All rooms have at least one {@link Room.Exit}<p>
		 * No two {@link ItemContainer}s contain the same item or has an {@link ItemContainer#amount} of 0 or less.<p>
		 * All {@link Player}s in the room are logged in.
		*/
		ArrayList<ItemContainer> delete_us = new ArrayList<ItemContainer>();
		boolean name_found;
		for (Room r : world.getRooms()) {
			if(r.getExits().isEmpty()){
				throw new UnrecoverableInvariantViolation("Room "+r.getName()+" has no exits. The room is pointless.");
			}
//			for (ItemContainer ic : r.getItems()) {
//				if(!World.assertExistence(ic.getName(), world.getItems())){
//					if(strict){
//						throw new InvariantViolation("Item "+ic.getName()+" does not exist as a propper item");
//					}else{
//						ic.setAmount(0);
//						report.add("Item "+ic.getName()+" does not exist as a propper item. Resolving by deleteing");
//					}
//				}
//				if(ic.getAmount() <= 0){
//					if(strict){
//						throw new InvariantViolation("Item "+ic.getName()+" has an amount of zero or less.");
//					}
//				}
//				name_found = false;
//				for (ItemContainer ic2 : r.getItems()) {
//					if(ic.getName().equals(ic2.getName())){
//						if(name_found){
//							if(strict){
//								throw new InvariantViolation(ic.getName()+" is in more than one item container!");
//							}else{
//								ic.addAmount(ic2.getAmount());
//								ic2.setAmount(0);
//								report.add(ic.getName()+" i spresent in more than two item containers. Resolving by merging");
//							}
//						}else{
//							name_found = true;
//						}
//					}
//				}
//				
//			}
			HashMap<String, ItemContainer> clean_set = new HashMap<String, ItemContainer>();
			for (ItemContainer ic : r.getItems()) {
				if(clean_set.containsKey(ic.getName())){
					if(strict){
						throw new InvariantViolation(ic.getName()+" was found in two containers.");
					}else{
						report.add(ic.getName()+" was found in more than one container. Resolving by merging.");
						clean_set.get(ic.getName()).addAmount(ic.getAmount());
					}
				}else if(ic.getAmount() < 0){
					if(strict){
						throw new InvariantViolation(ic.getName()+" has an amount of zero or less.");
					}else{
						report.add( ic.getName()+" has an amount of zero or less. Resolving by deleting.");
					}
				}else if(!World.assertExistence(ic.getName(), world.getItems())){
					if(strict){
						throw new InvariantViolation(ic.getName()+" isn't really in the world.");
					}else{
						report.add(ic.getName()+" isn't really in the world. Resolving by deleting");
					}
				}else{
					clean_set.put(ic.getName(), ic);
				}
			}
			r.getItems().removeAll(r.getItems());
			r.getItems().addAll(clean_set.values());
			
			for(Player p : r.getPlayers()){
				if(!p.isLoggedIn()){
					if(strict){
						throw new InvariantViolation("Player "+p.getName()+" is not loged in.");
					}else{
						try {
							r.removePlayer(p);
						} catch (EntityNotPresent e) {
							throw new UnrecoverableInvariantViolation("Tried to remove noxesting player.");
						}
						report.add("Player "+p.getName()+" is not loged in. Resolved by removing player.");
					}
				}
			}
		}
		
		return report.toArray(new String[0]);
		
	}
	
	public String[] checkPlayersInvariant(World world, Boolean strict) throws UnrecoverableInvariantViolation, InvariantViolation{
		
		ArrayList<String> report = new ArrayList<>();
		
		checkNameCollisions(world.getPlayers());
		
		boolean loggedin;
		ArrayList<Room> occupied_rooms;
		for (Player p : world.getPlayers()){
			occupied_rooms = new ArrayList<Room>();
			
			//Check health invariant
			if(p.getCs().getHealth() <=0){
				if(strict){
					throw new InvariantViolation("Player " + p.getName() + " has health " + p.getCs().getHealth());
				}else{
					report.add("Player " + p.getName() + " had less than 0 health. Resolved by setting health to 1");
					p.getCs().setHealth(1);
				}
			}
			
			//Look for presence in multiple rooms.
			for (Room r : world.getRooms()) {
				for(Player p1 : r.getPlayers()){
					if(p1.getName().equals(p.getName())){
						occupied_rooms.add(r);
						if(occupied_rooms.size() >1){
							if(strict){
								throw new InvariantViolation("Player "+p.getName()+ " was found in more than one room.");
							}
						}
					}
				}
			}
			
			//Player in more than one room
			if(occupied_rooms.size() > 1){
				//No need to do a strict check here.
				for (Room r : occupied_rooms) {
					try {
						r.removePlayer(p);
					} catch (EntityNotPresent e) {
						throw new InvariantViolation("Tried to remove player from a room in wich he is not.");
					}
				}
				if(p.isLoggedIn()){
					p.setLocation(world.getLobby(p.getCs().getLevel()));
					world.getLobby(p.getCs().getLevel()).addPlayer(p);
					report.add("Player "+p.getName()+" was found in multiple rooms and is logged in. Resolved by sending the player to lobby("+p.getLocation().getName()+") and removing the player from the rooms.");
				}else{
					report.add("Player "+p.getName()+" was found in multiple rooms and is loged out. Resolved by removing the player from the rooms");
	
				}
			//Player in no room but logged in (implicitly tests that player is present in his own room)
			}else if(occupied_rooms.size() == 0 && p.isLoggedIn()){
				if(strict){
					throw new InvariantViolation("Player "+p.getName()+" is loggen in but not in any room");
				}else{
					p.setLocation(world.getLobby(p.getCs().getLevel()));
					p.getLocation().addPlayer(p);
					report.add("Player "+p.getName()+" is loged in but not in any room. Resolved by moving player to the lobby("+p.getLocation().getName()+").");
				}
			//player in room but not logged in
			}else if(occupied_rooms.size() == 1 && !p.isLoggedIn()){
				if(strict){
					throw new InvariantViolation("Player "+p.getName()+" is present in a room");
				}else{
					report.add("Player "+p.getName()+" is loged out but present in a room. Resolved by removing the player from the room.");
					try {
						p.getLocation().removePlayer(p);
					} catch (EntityNotPresent e) {
						throw new UnrecoverableInvariantViolation("Tried to remove a player from a room in wich he is not.");
					}
				}
			//Player logged in but not present in his own room.
			}
//			else if(!p.getLocation().getPlayers().contains(p) && p.isLoggedIn()){
//				if(strict){
//					throw new InvariantViolation("Player "+p.getName()+" is not present in his own room.");
//				}else{
//					p.getLocation().addPlayer(p);
//					report.add("Player "+p.getName()+" was not present in his own room. Fixed by adding him to the room.");
//				}
//			}
			
			
			
		}
		
		return report.toArray(new String[0]);
	}
	
	
	
	/**
	 * 
	 * Checks that there are no name collisions in the entire world.
	 * 
	 * @return True if no names collide 
	 * @throws UnrecoverableInvariantViolation If a name collision exists
	 */
	public  Boolean checkGlobalNameSpace(World world) throws UnrecoverableInvariantViolation{
		HashSet<Entity> monster_set = new HashSet<>();
		monster_set.addAll(world.getItems());
		monster_set.addAll(world.getPlayers());
		monster_set.addAll(world.getNpcs());
		monster_set.addAll(world.getRooms());
		Boolean name_found = false;
		for (Entity e1 : monster_set) {
			name_found = false;
			for (Entity e2 : monster_set) {
				if(e1.getName().equals(e2.getName())){
					if(name_found){
						throw new UnrecoverableInvariantViolation(e1.getClass().getSimpleName()+":"+e1.getName() + " has the same name as " + e2.getClass().getSimpleName()+":"+e2.getName());
					}else{
						name_found = true;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * Checks that there are no name collisions in the set.
	 * 
	 * @param set The set whose namespace is to be tested
	 * @return True if no names collide 
	 * @throws UnrecoverableInvariantViolation If a name collision exists
	 */
	//TODO this can be done in linear time trough building up a HashSet and checking for existence.
	public  Boolean checkNameCollisions(HashSet<? extends Entity> set) throws UnrecoverableInvariantViolation {
		if(set.isEmpty()){return true;}
		
		boolean name_found = false;
		
		for (Entity e1 : set) {
			name_found = false;
			for (Entity e2 : set) {
				if(e1.getName().equals(e2.getName())){
					if(name_found){
						throw new UnrecoverableInvariantViolation(e1.getClass().getSimpleName()+":"+e1.getName() + " has the same name as " + e2.getClass().getSimpleName()+":"+e2.getName());
					}else{
						name_found = true;
					}
				}
			}
		}
		
		return true;
		
	}
	
	/** 
	 * These exceptions are to be thrown when an invariant violation has occurred and there is
	 * no viable option to restore the invariant.
	 * 
	 * @author TheGrandmother
	 */
	@SuppressWarnings("serial")
	public class UnrecoverableInvariantViolation extends Exception{
		public UnrecoverableInvariantViolation(String message) {
			super(message);
		}
		
	}
	
	/**
	 * These exceptions are to be thrown when an invariant violation has occurred that can be
	 * handled in a sensible fashion.
	 * 
	 * @author TheGrandmother
	 */
	@SuppressWarnings("serial")
	public class InvariantViolation extends Exception{
		public InvariantViolation(String message) {
			super(message);
		}
	}
	
}









