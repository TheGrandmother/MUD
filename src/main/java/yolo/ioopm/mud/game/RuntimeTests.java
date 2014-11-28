package yolo.ioopm.mud.game;

import java.util.ArrayList;
import java.util.HashSet;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.exceptions.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.Player;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;


/**
 *This class is used to perform invariant checks at runtime.
 * 
 * @author TheGrandmother
 */
public class RuntimeTests {

	public RuntimeTests() {
	
	}
	
	
	public String[] checkPlayersInvariant(World world, Boolean strict) throws UnrecoverableInvariantViolation, InvariantViolation{
		
		ArrayList<String> report = new ArrayList<>();
		
		checkNameCollisions(world.getPlayers());
		
		boolean loggedin;
		ArrayList<Room> occupied_rooms;
		for (Player p : world.getPlayers()){
			occupied_rooms = new ArrayList<Room>();
			
			//Cehck health invariant
			if(p.getCs().getHealth() <=0){
				if(strict){
					throw new InvariantViolation("Player " + p.getName() + "has health " + p.getCs().getHealth());
				}else{
					report.add(" Player " + p.getName() + " had less than 0 health. Resolved by setting health to 1");
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
					report.add("Player "+p.getName()+" was found in multiple rooms and is logged in. Resolved by sending the player to lobby and removing the player from the rooms.");
				}else{
					report.add("Player "+p.getName()+" was found in multiple rooms and is loged out. Resolved by removing the player from the rooms");
	
				}
			//Player in no room but logged in (implicitly tests that player is present in his own room)
			}else if(occupied_rooms.size() == 0 && p.isLoggedIn()){
				if(strict){
					throw new InvariantViolation("Player "+p.getName()+" is loggen in but not in any room");
				}else{
					report.add("Player "+p.getName()+" is loged in but not in any room. Resolved by moving player to the lobby.");
					p.setLocation(world.getLobby(p.getCs().getLevel()));
					p.getLocation().addPlayer(p);
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









