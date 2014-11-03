package yolo.ioopm.mud.generalobjects;

public class Pc extends Character {

	Boolean active;
	boolean is_admin;

	public Pc(String name, String description, Room starting_location) {
		super(name, description, starting_location);
		// TODO Auto-generated constructor stub
		is_admin = false;
	}
}
