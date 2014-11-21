package yolo.ioopm.mud.generalobjects;

public class Pc extends Character {

	private boolean logged_in;
	private final String password;
	private boolean is_admin;

	/**
	 * 
	 * 
	 * 
	 * @param name
	 * @param description
	 * @param starting_location
	 */
	public Pc(String name, String description, String password,Room starting_location) {
		super(name, description, starting_location);
		//starting_location.addPlayer(this);
		logged_in =  false;
		this.password = password;
		is_admin = false;
	}
	
	//public void setPassword(String password){
	//	this.password = password;
	//}
	
	public boolean checkPassword(String password){
		return this.password.equals(password);
	}
	
	public void setAdmin(boolean is_admin) {
		this.is_admin = is_admin;
	}
	
	public void setLoggedIn(boolean logged_in) {
		this.logged_in = logged_in;
	}
	
	public boolean isAdmin(){
		return is_admin;
	}
	
	public boolean isLogedIn(){
		return this.logged_in;
	}
	
	
	
	
}
