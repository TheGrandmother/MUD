package yolo.ioopm.mud.generalobjects;

public class Npc extends Character {

	public Npc(String name, String description, Room starting_location) {
		super(name, description, starting_location);
		location.addNPC(this);
		// TODO Auto-generated constructor stub
	}
}
