package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

/**
 * This class specifies a Character. Will be extended by other classes for PC's and NPC's.
 * <p/>
 * Each character must always be bound to one specific room.
 *
 * @author TheGrandmother
 */

public abstract class Character extends Entity {

	private final String         NAME;
	private       String         description;
	private       Room           location;
	private       Inventory      inventory;
	private       CharacterSheet cs;
	private       boolean        lives;

	/**
	 * Constructs a character.
	 *
	 * @param name              The name of the character. Needs necessarily not be unique.
	 * @param starting_location The starting room for the character.
	 */
	public Character(String name, String description, Room starting_location) {
		this.NAME = name;
		this.description = description;
		location = starting_location;
		inventory = new Inventory();
		cs = new CharacterSheet();
		lives = true;


	}

	public String getDescription() {
		return description;
	}

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
		return NAME;
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

	public CharacterSheet getCs() {
		return cs;
	}


	/**
	 * The CharcterSheet contains information about the characters current status.
	 *
	 * @author TheGrandmother
	 */
	public class CharacterSheet {
		private int hp;
		private int health;
		private int max_health;
		private int level;

		/**
		 * Does not do much interesting. The class which inherits from the Character class
		 * Defines the initial configuration of the Character sheet .
		 */
		public CharacterSheet() {

		}

		/**
		 * @return The hp (university credits) of the character
		 */
		public int getHp() {
			return hp;
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
		public int getMax_health() {
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

		HashSet<ItemContainer> items;
		private int volume;
		private int max_volume = 10;

		public Inventory() {
			volume = 0;
			items = new HashSet<ItemContainer>();
		}

		
		/**
		 * Tries to retrive an item from the items set.
		 * NOTE: this does not remove the item!
		 * 
		 * @param name
		 * @param type
		 * @return Returns null if no silly item was found
		 */
		public Item findItem(String name, Item.Type type){
			for (ItemContainer i : items) {
				if(type == i.getType() && i.getName().equals(name)){
					return i.getItem();
				}
			}
			return null;
			
		}
		
		/**
		 * Tries to remove the item. Either removes the item completely or just decreases the amount.
		 * 
		 * @param name
		 * @param type
		 * @return false if item does not exist
		 */
		public boolean removeItem(String name, Item.Type type){
			
			for (ItemContainer i : items) {
				if(type == i.getType() && i.getName().equals(name)){
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
		 * 
		 * Ads an item to the inventory
		 * 
		 * @param item item to be added
		 * @throws InventoryOverflow gets thrown if item does not fit.
		 */
		public void addItem(Item item) throws InventoryOverflow{
			for (ItemContainer i : items) {
				if(item.getType() == i.getType() && item.getName().equals(i.getName())){
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

		public int getMax_volume() {
			return max_volume;
		}

		public int getVolume() {
			return volume;
		}

		public void setMax_volume(int max_volume) {
			this.max_volume = max_volume;
		}

		public void setVolume(int volume) {
			this.volume = volume;
		}


		
		@SuppressWarnings("serial")
		public class InventoryOverflow extends Exception{
			public InventoryOverflow() {
				super();
			}
		}
		
	
		
		
	}


}
