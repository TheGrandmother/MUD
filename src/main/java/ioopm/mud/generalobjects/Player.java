package ioopm.mud.generalobjects;

/**
 * This is the player class which defines a Player. A Player is character which is played by a human user.
 * In contrast to a NPC which is not played by a character.
 *
 * @author TheGrandmother
 */
public class Player extends Character {

	private final String password;
	private boolean logged_in;
	private boolean is_admin;
	private boolean is_banned;

	/**
	 * Creates a new player. With a given name and a given description.
	 *
	 * @param name              The name of the new player
	 * @param description       The description of the player
	 * @param starting_location Where the player starts <b>Note:</b> This should be the lobby for a level 1 character.
	 */
	public Player(String name, String description, String password, Room starting_location) {
		super(name, description, starting_location);
		location.addPlayer(this);
		//starting_location.addPlayer(this);
		logged_in = false;
		this.password = password;
		is_admin = false;
		is_banned = false;
	}

	/**
	 * <b>NOTE:</b> This is the most insecure password system in the known universe.
	 *
	 * @param password the password to be checked.
	 * @return true if the given password corresponds to the players password.
	 */
	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	/**
	 * Checks if the player has admin status
	 *
	 * @return true if the player is admin.
	 */
	public boolean isAdmin() {
		return is_admin;
	}

	/**
	 * This sets the players admin status.
	 *
	 * @param is_admin if the player should be admin.
	 */
	public void setAdmin(boolean is_admin) {
		this.is_admin = is_admin;
	}
	
	/**
	 * Checks if the player is banned
	 *
	 * @return true if the player is banned.
	 */
	public boolean isBanned() {
		return is_banned;
	}

	/**
	 * This sets the players banned status.
	 *
	 * @param is_banned if the player should be banned.
	 */
	public void setBanned(boolean banned) {
		this.is_banned = banned;
	}

	/**
	 * Checks if the player is logged in.
	 *
	 * @return true if the player is logged in otherwise false.
	 */
	public boolean isLoggedIn() {
		return this.logged_in;
	}

	/**
	 * Sets the logged in status of the player.
	 *
	 * @param logged_in If the player is logged in.
	 */
	public void setLoggedIn(boolean logged_in) {
		this.logged_in = logged_in;
	}


}
