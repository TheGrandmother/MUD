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
			
			if(p.getCs().getHealth() <=0){
				if(strict){
					throw new InvariantViolation("Player " + p.getName() + "has health " + p.getCs().getHealth());
				}else{
					report.add(" Player " + p.getName() + " had less than 0 health. Resolved by setting health to 1");
					p.getCs().setHealth(1);
				}
			}
			
			for (Room r : world.getRooms()) {
				for(Player p1 : r.getPlayers()){
					if(p1.getName().equals(p.getName())){
						occupied_rooms.add(r);
						if(p.isLoggedIn() && occupied_rooms.size() >1){
							if(strict){
								throw new InvariantViolation("Player "+p.getName()+ " was found in more than one room.");
							}
						}else{
							if(strict){
								throw new InvariantViolation("Player "+p.getName()+" is logged out but present in a room");
							}
						}
					}
				}
			}
			for (Room r : occupied_rooms) {
				try {
					r.removePlayer(p);
				} catch (EntityNotPresent e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
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









