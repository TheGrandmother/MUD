package ioopm.mud.generalobjects;

import ioopm.mud.generalobjects.items.Weapon;

/**
 * This class specifies a Character. Will be extended by other classes for PC's and NPC's.
 * <p>
 * <b>NOTE:</b> Even though a player always has a location that doesn't mean that the player is in the room.
 *
 * @author TheGrandmother
 */

public abstract class Character extends Entity {


	protected Room location;
	private String description;
	private Inventory inventory;
	private CharacterSheet cs;
	private boolean lives;
	private Weapon weapon = null;

	/**
	 * Constructs a character. With an empty inventory and a default CharacterSheet
	 *
	 * @param name              The name of the character. Needs necessarily not be unique.
	 * @param starting_location The starting room for the character.
	 */
	public Character(String name, String description, Room starting_location) {
		super(name);
		this.description = description;
		location = starting_location;

		inventory = new Inventory();
		cs = new CharacterSheet();
		lives = true;


	}

	/**
	 * Returns the players mounted weapon.
	 *
	 * @return the players equipped weapon(may be <code>null</code>)
	 */
	public Weapon getWeapon() {
		return weapon;
	}

	/**
	 * Equipped a weapon!
	 *
	 * @param weapon Sets the equipped weapon. (May be <code>null</code>)
	 */
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	/**
	 * returns the description of the character
	 *
	 * @return the description of the character
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Alters the description of the character
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return The current Room in which the character is.
	 */
	public Room getLocation() {
		return location;
	}

	/**
	 * Changes the characters location
	 *
	 * @param location
	 */
	public void setLocation(Room location) {
		this.location = location;
	}

	/**
	 * @return The name of the character.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The inventory of the character.
	 */

	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Returns the character sheet of the player
	 *
	 * @return
	 */
	public CharacterSheet getCs() {
		return cs;
	}


}
