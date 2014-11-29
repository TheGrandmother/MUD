package ioopm.mud.generalobjects;

import java.util.HashSet;

import ioopm.mud.generalobjects.items.Weapon;

/**
 * This class specifies a Character. Will be extended by other classes for PC's and NPC's.
 * <p>
 * <b>NOTE:</b> Even though a player always has a location that doesn't mean that the player is in the room.
 * @author TheGrandmother
 */

public abstract class Character extends Entity {


	private   String         description;
	protected Room           location;
	private   Inventory      inventory;
	private   CharacterSheet cs;
	private   boolean        lives;
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
	 * @return the players equipped weapon(may be <code>null</code>)
	 */
	public Weapon getWeapon(){
		return weapon;
	}
	
	/**
	 * Equipped a weapon!
	 * @param weapon Sets the equipped weapon. (May be <code>null</code>) 
	 */
	public void setWeapon(Weapon weapon){
		this.weapon = weapon;
	}
	
	/**
	 * returns the description of the character
	 * @return the description of the character
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Alters the description of the character
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
	 * @return The name of the character.
	 */
	public String getName() {
		return name;
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
	 * @return The inventory of the character.
	 */

	public Inventory getInventory() {
		return inventory;
	}
	/**
	 * Returns the character sheet of the player
	 * @return
	 */
	public CharacterSheet getCs() {
		return cs;
	}


	
	/**
	 * The CharacterSheet contains information about the characters current status.
	 *<p>
	 *<b>NOTE:</b> hp denotes university credits which is so dumb its insane. This is due to the original specification being in Swedish. One day.... it shall be refactored.
	 * @author TheGrandmother
	 */
	public class CharacterSheet {
		private final int BASE_HP = 30;
		private final double HP_FACTOR = 1.5;
		private final double HEALTH_FACTOR = 1.3;
		private int hp;
		private int health;
		private int max_health;
		private int level;

		/**
		 * Creates a new default character sheet with<p>
		 * Level = 1<p>
		 * Health = 50<p>
		 * Max_health = 50<p>
		 * Hp = 30<p>
		 */
		public CharacterSheet() {
			this.hp = BASE_HP;
			this.level = 1;
			this.health = 50;
			this.max_health = 50;
			this.hp = 30;

		}
		
		/**
		 * @return The hp (university credits) of the character
		 */
		public int getHp() {
			return hp;
		}
		
		/**
		 * Computes how many university credits left to the next level.<p>
		 * <b>NOTE:</b> This needs to correspond with how {@link CharacterSheet#levelUp()} works.
		 * @return how many hp left until the new level is reached.
		 */
		public int hpToNextLevel(){
			return (int)(BASE_HP*(HP_FACTOR*level)) - hp;
		}
		
		/**
		 * Checks if the player can be leveled up.<p>
		 * The mechanics of leveling will be described in the specs/Mechanics.txt file.<p>
		 * If the level-up is successful the level will be incremented and a new max health will be set
		 * and full health will be restored.
		 * 
		 * @return true if the player leveled up. False otherwise.
		 */
		//TODO This seriously needs to be reworked!
		public boolean levelUp(){
			if(hp >= BASE_HP*(HP_FACTOR*level)){
				level++;
				max_health = (int) (max_health*(HEALTH_FACTOR));
				health = max_health;
				return true;
			}
			return false;
		}

		/**
		 * sets the hp (university credits)
		 *
		 * @param hp
		 */
		public void setHp(int hp) {
			this.hp = hp;
		}
		
		/**
		 * Adds or subtracts hp. Can't set hp to less than 0
		 * @param hp
		 */
		public void addHp(int hp){
			if(this.hp + hp < 0){
				this.hp = 0;
			}else{
				this.hp += hp;
			}
		}

		/**
		 * @return The characters health
		 */
		public int getHealth() {
			return health;
		}

		/**
		 * Tries to set the Characters health to the number given as argument.
		 * If the argument given is greater than the max health of the character
		 * the character will get its maximum health.
		 *
		 * @param health
		 */
		public void setHealth(int health) {
			if(health + this.health > this.max_health) {
				this.health = this.max_health;
			}
			else {
				this.health = health;
			}
		}

		/**
		 * Adds the amount of health. Which can also be negative.
		 * <p/>
		 * If the new health is greater than the maximum health of the
		 * character the characters health will be its maximum health.
		 * If it where to be less than zero the characters health will be set to zero.
		 *
		 * @param amount
		 */
		public void addHealth(int amount) {
			if(this.health + amount > this.max_health) {
				this.health = this.max_health;
			}
			else if(this.health + amount < 0) {
				this.health = 0;
			}
			else {
				this.health += amount;
			}
		}

		/**
		 * Sets the maximum health
		 *
		 * @param max_health
		 */
		public void setMax_health(int max_health) {
			this.max_health = max_health;
		}

		/**
		 * gets the maximum health.
		 *
		 * @return
		 */
		public int getMaxHealth() {
			return max_health;
		}

		/**
		 * Sets the level of the character
		 *
		 * @param level
		 */
		public void setLevel(int level) {
			this.level = level;
		}

		/**
		 * @return The current level of the character
		 */
		public int getLevel() {
			return level;
		}


	}

	/**
	 * The inventory contains and manages all of the items possessed by the Character.
	 *
	 * @author TheGrandmother
	 */
	 public  class Inventory {

		private HashSet<ItemContainer> items;
		private int volume;
		private int max_volume = 10;

		/**
		 * Creates a new empty inventory with max_volume set to 10.
		 */
		public Inventory() {
			volume = 0;
			items = new HashSet<ItemContainer>();
		}

		/**
		 * returns the set of ItemContainers
		 * @return The set of item containers.
		 */
		public HashSet<ItemContainer> getitems(){
			return this.items;
		}
		
		/**
		 * Tries to retrieve an item from the items set.
		 * NOTE: this does not remove the item!
		 * 
		 * @param name the name of the item
		 * @return Returns null if no silly item was found
		 */
		public Item findItem(String name){
			for (ItemContainer i : items) {
				if( i.getName().equals(name)){
					return i.getItem();
				}
			}
			return null;
			
		}
		
		/**
		 * Tries to remove the item. Either removes the {@link ItemContainer} completely or just decreases the amount.
		 * 
		 * @param name The name of the item.
		 * @return false if item does not exist
		 */
		public boolean removeItem(String name){
			
			for (ItemContainer i : items) {
				if(i.getName().equals(name)){
					if(i.getAmount() == 1){
						volume -= i.getItem().getSize();
						items.remove(i);
						return true;
					}else{
						volume -= i.getItem().getSize();
						i.setAmount(i.getAmount()-1);
						return true;
					}
				}
			}
			
			return false;
		}

		
		/**
		 * Ads an item to the inventory. Creates a new {@link ItemContainer} or just dereases the amount.
		 * 
		 * @param item item to be added
		 * @throws InventoryOverflow gets thrown if item does not fit.
		 */
		public void addItem(Item item) throws InventoryOverflow{
			for (ItemContainer i : items) {
				if(item.getName().equals(i.getName())){
					if(item.getSize()+volume > max_volume){
						throw new InventoryOverflow();
					}else{
						volume += item.getSize();
						i.setAmount(i.getAmount()+1);
					}
				}
			}
			if(item.getSize()+volume > max_volume){
				throw new InventoryOverflow();
			}else{
				volume += item.getSize();
				items.add(new ItemContainer(item));
			}
			
		}
		
		/**
		 * 
		 * @return the maximum volume of the inventory
		 */
		public int getMax_volume() {
			return max_volume;
		}

		/**
		 * How much space is occupied
		 * 
		 * @return the current volume of the inventory
		 */
		public int getVolume() {
			return volume;
		}

		/**
		 * increases the capacity of the inventory
		 * @param max_volume new max volume
		 */
		public void setMax_volume(int max_volume) {
			this.max_volume = max_volume;
		}
		/**
		 * sets the current volume
		 * 
		 * @param volume
		 */
		public void setVolume(int volume) {
			this.volume = volume;
		}


		
		@SuppressWarnings("serial")
		/**
		 * 
		 * This exception is to be thrown when an item does not fit into the inventory.
		 * 
		 * @author TheGrandmother
		 */
		public class InventoryOverflow extends Exception{
			public InventoryOverflow() {
				super();
			}
		}
		
	
		
		
	}


}
