package ioopm.mud.game;

import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.*;
import ioopm.mud.generalobjects.Room.Exit;
import ioopm.mud.generalobjects.items.Key;
import ioopm.mud.generalobjects.items.Weapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * This class is used to perform invariant checks at runtime.
 * It can also resolve some invariant violations at runtime.
 *
 * @author TheGrandmother
 */
public class RuntimeTests {

	public RuntimeTests() {

	}

	/**
	 * Checks for invariant violations for the {@link Item}s in the world.<br>
	 * The invariants are specified in {@link World}
	 *
	 * @param world  The world whose invariants are to be tested.
	 * @param strict If set to true no attempts to fix the problem will be made.
	 * @return A an array of strings describing what was done to resolve the issues. If all was fine an array of length 0 will be returned.
	 * @throws UnrecoverableInvariantViolation If a violation appears that is not resolvable.
	 * @throws InvariantViolation              Gets thrown for recoverable violations if the strict flag is set to true.
	 */
	public String[] checkItemInvariant(World world, Boolean strict) throws UnrecoverableInvariantViolation, InvariantViolation {
		ArrayList<String> report = new ArrayList<>();
		checkNameCollisions(world.getItems());

		for(Item i : world.getItems()) {
			if(i.getSize() < 0) {
				if(strict) {
					throw new InvariantViolation("Item " + i.getName() + " has negative size.");
				} else {
					removeItemFromAllRooms(i, world);
					report.add("Item " + i.getName() + " has negative size. Resolved by removing");
				}
			} else if(i instanceof Weapon) {
				Weapon w = (Weapon) i;
				if(w.getDamage() <= 0) {
					if(strict) {
						throw new InvariantViolation("The weapon " + i.getName() + " has damage of 0 or less.");
					} else {
						removeItemFromAllRooms(i, world);
						report.add("The weapon " + i.getName() + " has damage of 0 or less. Resolved by removing");
					}
				}
			} else if(i instanceof Key) {

			}
		}


		return report.toArray(new String[0]);
	}

	/**
	 * Removes an item from all the room in which it is present
	 *
	 * @param i     The item
	 * @param world The world from which it is to be removed.
	 */
	private void removeItemFromAllRooms(Item i, World world) {
		for(Room r : world.getRooms()) {
			try {
				r.removeItemCompletley(i);
			} catch(EntityNotPresent e) {
			}
		}
	}

	/**
	 * Checks for invariant violations for the {@link Room}s in the world.<br>
	 * The invariants are specified in {@link World}
	 *
	 * @param world  The world whose invariants are to be tested.
	 * @param strict If set to true no attempts to fix the problem will be made.
	 * @return A an array of strings describing what was done to resolve the issues. If all was fine an array of length 0 will be returned.
	 * @throws UnrecoverableInvariantViolation If a violation appears that is not resolvable.
	 * @throws InvariantViolation              Gets thrown for recoverable violations if the strict flag is set to true.
	 */
	public String[] checkRoomInvariant(World world, Boolean strict) throws UnrecoverableInvariantViolation, InvariantViolation {
		ArrayList<String> report = new ArrayList<>();

		checkNameCollisions(world.getRooms());


		for(Room r : world.getRooms()) {
			//Check for empty or bad exits
			if(r.getExits().isEmpty()) {
				throw new UnrecoverableInvariantViolation("Room " + r.getName() + " has no exits. The room is pointless.");
			} else {
				for(Exit e : r.getExits()) {
					if(!World.assertExistence(e.getNameOfOtherside(), world.getRooms())) {
						throw new UnrecoverableInvariantViolation("Room has an exit leading to " + e.getNameOfOtherside() + " which is not a real room.");
					}
				}
			}
			//Check validity of items.
			HashMap<String, ItemContainer> clean_set = new HashMap<String, ItemContainer>();
			for(ItemContainer ic : r.getItems()) {
				if(clean_set.containsKey(ic.getName())) {
					if(strict) {
						throw new InvariantViolation(ic.getName() + " was found in two containers.");
					} else {
						report.add(ic.getName() + " was found in more than one container. Resolving by merging.");
						clean_set.get(ic.getName()).addAmount(ic.getAmount());
					}
				} else if(ic.getAmount() < 0) {
					if(strict) {
						throw new InvariantViolation(ic.getName() + " has an amount of zero or less.");
					} else {
						report.add(ic.getName() + " has an amount of zero or less. Resolving by deleting.");
					}
				} else if(!World.assertExistence(ic.getName(), world.getItems())) {
					if(strict) {
						throw new InvariantViolation(ic.getName() + " isn't really in the world.");
					} else {
						report.add(ic.getName() + " isn't really in the world. Resolving by deleting");
					}
				} else {
					clean_set.put(ic.getName(), ic);
				}
			}
			r.getItems().removeAll(r.getItems());
			r.getItems().addAll(clean_set.values());

			for(Player p : r.getPlayers()) {
				if(!p.isLoggedIn()) {
					if(strict) {
						throw new InvariantViolation("Player " + p.getName() + " is not loged in.");
					} else {
						try {
							r.removePlayer(p);
						} catch(EntityNotPresent e) {
							throw new UnrecoverableInvariantViolation("Tried to remove noxesting player.");
						}
						report.add("Player " + p.getName() + " is not loged in. Resolved by removing player.");
					}
				}
			}
		}

		return report.toArray(new String[0]);

	}

	/**
	 * Checks for invariant violations for the {@link Player}s in the world.<br>
	 * The invariants are specified in {@link World}
	 *
	 * @param world  The world whose invariants are to be tested.
	 * @param strict If set to true no attempts to fix the problem will be made.
	 * @return A an array of strings describing what was done to resolve the issues. If all was fine an array of length 0 will be returned.
	 * @throws UnrecoverableInvariantViolation If a violation appears that is not resolvable.
	 * @throws InvariantViolation              Gets thrown for recoverable violations if the strict flag is set to true.
	 */
	public String[] checkPlayersInvariant(World world, Boolean strict) throws UnrecoverableInvariantViolation, InvariantViolation {

		ArrayList<String> report = new ArrayList<>();

		checkNameCollisions(world.getPlayers());

		boolean loggedin;
		ArrayList<Room> occupied_rooms;
		for(Player p : world.getPlayers()) {
			occupied_rooms = new ArrayList<Room>();

			//Check health invariant
			if(p.getCs().getHealth() <= 0) {
				if(strict) {
					throw new InvariantViolation("Player " + p.getName() + " has health " + p.getCs().getHealth());
				} else {
					report.add("Player " + p.getName() + " had less than 0 health. Resolved by setting health to 1");
					p.getCs().setHealth(1);
				}
			}

			//Look for presence in multiple rooms.
			for(Room r : world.getRooms()) {
				for(Player p1 : r.getPlayers()) {
					if(p1.getName().equals(p.getName())) {
						occupied_rooms.add(r);
						if(occupied_rooms.size() > 1) {
							if(strict) {
								throw new InvariantViolation("Player " + p.getName() + " was found in more than one room.");
							}
						}
					}
				}
			}

			//Player in more than one room
			if(occupied_rooms.size() > 1) {
				//No need to do a strict check here.
				for(Room r : occupied_rooms) {
					try {
						r.removePlayer(p);
					} catch(EntityNotPresent e) {
						throw new InvariantViolation("Tried to remove player from a room in wich he is not.");
					}
				}
				if(p.isLoggedIn()) {
					p.setLocation(world.getLobby(p.getCs().getLevel()));
					world.getLobby(p.getCs().getLevel()).addPlayer(p);
					report.add("Player " + p.getName() + " was found in multiple rooms and is logged in. Resolved by sending the player to lobby(" + p.getLocation().getName() + ") and removing the player from the rooms.");
				} else {
					report.add("Player " + p.getName() + " was found in multiple rooms and is loged out. Resolved by removing the player from the rooms");

				}
				//Player in no room but logged in (implicitly tests that player is present in his own room)
			} else if(occupied_rooms.size() == 0 && p.isLoggedIn()) {
				if(strict) {
					throw new InvariantViolation("Player " + p.getName() + " is loggen in but not in any room");
				} else {
					p.setLocation(world.getLobby(p.getCs().getLevel()));
					p.getLocation().addPlayer(p);
					report.add("Player " + p.getName() + " is loged in but not in any room. Resolved by moving player to the lobby(" + p.getLocation().getName() + ").");
				}
				//player in room but not logged in
			} else if(occupied_rooms.size() == 1 && !p.isLoggedIn()) {
				if(strict) {
					throw new InvariantViolation("Player " + p.getName() + " is present in a room");
				} else {
					report.add("Player " + p.getName() + " is loged out but present in a room. Resolved by removing the player from the room.");
					try {
						p.getLocation().removePlayer(p);
					} catch(EntityNotPresent e) {
						throw new UnrecoverableInvariantViolation("Tried to remove a player from a room in wich he is not.");
					}
				}
			}
		}

		return report.toArray(new String[0]);
	}


	/**
	 * Checks that there are no name collisions in the entire world.
	 *
	 * @return True if no names collide
	 * @throws UnrecoverableInvariantViolation If a name collision exists
	 */
	public Boolean checkGlobalNameSpace(World world) throws UnrecoverableInvariantViolation {
		HashSet<Entity> monster_set = new HashSet<>();
		monster_set.addAll(world.getItems());
		monster_set.addAll(world.getPlayers());
		monster_set.addAll(world.getNpcs());
		monster_set.addAll(world.getRooms());
		Boolean name_found = false;
		for(Entity e1 : monster_set) {
			name_found = false;
			for(Entity e2 : monster_set) {
				if(e1.getName().equals(e2.getName())) {
					if(name_found) {
						throw new UnrecoverableInvariantViolation(e1.getClass().getSimpleName() + ":" + e1.getName() + " has the same name as " + e2.getClass().getSimpleName() + ":" + e2.getName());
					} else {
						name_found = true;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Checks that there are no name collisions in the set.
	 *
	 * @param set The set whose namespace is to be tested
	 * @return True if no names collide
	 * @throws UnrecoverableInvariantViolation If a name collision exists
	 */
	//TODO this can be done in linear time trough building up a HashSet and checking for existence.
	public Boolean checkNameCollisions(HashSet<? extends Entity> set) throws UnrecoverableInvariantViolation {
		if(set.isEmpty()) {
			return true;
		}

		boolean name_found = false;

		for(Entity e1 : set) {
			name_found = false;
			for(Entity e2 : set) {
				if(e1.getName().equals(e2.getName())) {
					if(name_found) {
						throw new UnrecoverableInvariantViolation(e1.getClass().getSimpleName() + ":" + e1.getName() + " has the same name as " + e2.getClass().getSimpleName() + ":" + e2.getName());
					} else {
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
	public class UnrecoverableInvariantViolation extends Exception {
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
	public class InvariantViolation extends Exception {
		public InvariantViolation(String message) {
			super(message);
		}
	}

}









