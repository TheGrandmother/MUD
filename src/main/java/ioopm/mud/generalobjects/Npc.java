package ioopm.mud.generalobjects;
/**
 * 
 * Class representing a non player character.<br>
 * NOT YET IMPLEMENTED.
 * 
 * @author heso8370
 *
 */
public class Npc extends Character {

	public Npc(String name, String description, Room starting_location) {
		super(name, description, starting_location);
		location.addNPC(this);
		// TODO Auto-generated constructor stub
	}
}
