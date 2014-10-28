package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

/**
 * 
 *This class specifies a Character. Will be extended by other classes for PC's and NPC's.
 *
 *Each character must always be bound to one specific room.
 * 
 * @author TheGrandmother
 *
 */

public abstract class Character extends Entity{
	
	private final String name;
	private Room location;
	private Inventory inventory;
	private CharacterSheet cs;
	private boolean lives;
	
	/**
	 * Constructs a character.
	 * 
	 * @param name	The name of the character. Needs necessarily not be unique.
	 * @param starting_location The starting room for the character.
	 */
	public Character(String name, Room starting_location){
		this.name = name;
		location = starting_location;
		inventory = new Inventory();
		cs = new CharacterSheet();
		lives = true;

		
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
	
	public CharacterSheet getCs() {
		return cs;
	}
	
	
	
	/**
	 * 
	 * The CharcterSheet contains information about the characters current status.
	 * 
	 * @author TheGrandmother
	 *
	 */
	class CharacterSheet{
		private int hp;
		private int health ;
		private int max_health;
		private int level;
		
		/**
		 * Does not do much interesting. The class which inherits from the Character class
		 * Defines the initial configuration of the Character sheet .
		 */
		public CharacterSheet(){
			
		}
	
		/**
		 * @return The hp (university credits) of the character
		 */
		public int getHp() {
			return hp;
		}
		/**
		 * sets the hp (university credits)
		 * @param hp
		 */
		public void setHp(int hp) {
			this.hp = hp;
		}
		
		/**
		 * 
		 * @return The characters health
		 */
		public int getHealth() {
			return health;
		}
		
		
		/**
		 * Tries to set the Characters health to the number given as argument.
		 * If the argument given is greater than the max health of the character
		 * the character will get its maximum health.
		 * @param health
		 */
		public void setHealth(int health) {
			if(health + this.health > this.max_health){
				this.health = this.max_health;
			}else{
				this.health = health;
			}
		}
		/**
		 * Adds the amount of health. Which can also be negative.
		 * 
		 * If the new health is greater than the maximum health of the 
		 * character the characters health will be its maximum health.
		 * If it where to be less than zero the characters health will be set to zero.
		 * 
		 * @param amount
		 */
		public void addHealth(int amount){
			if(this.health + amount > this.max_health){
				this.health = this.max_health;
			} else if(this.health + amount < 0){
				this.health = 0;
			} else{
				this.health += amount;
			}
		}
		
		/**
		 * Sets the maximum health
		 * @param max_health
		 */
		public void setMax_health(int max_health) {
			this.max_health = max_health;
		}
		
		/**
		 * gets the maximum health.
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
		 * 
		 * @return The current level of the character
		 */
		public int getLevel() {
			return level;
		}
		
		
		
		
	}
	
	/**
	 * 
	 * The inventory contains and manages all of the items possessed by the Character.
	 * 
	 * @author TheGrandmother
	 *
	 */
	class Inventory{
		
		HashSet<Item> items;
		private int volume;
		private int max_volume = 10;
		
		public Inventory(){
			volume = 0;
			items = new HashSet<Item>();
		}
		
		
		/**
		 * Decrements the number of uses for an item. Deletes item from list if uses is 0.
		 * Returns false if item is not found.
		 * 
		 * @param name The name of the item
		 * @return Returns false if the object was not found in the inventory.
		 */
		public boolean removeItem(String name){
			for (Item item : items) {
				if(item.getName() == name){
					if(item.getUses() == 0){
						volume -= item.getSize();
						items.remove(item);
						return true;
					}else{
						item.setUses(item.getUses()-1);
						return true;
					}
				}
			}
			return false;
		}
		
		//TODO Add item method.
		
		//TODO Write pickup method.
		
		//TODO Write drop method.
		
		/**
		 * Searches trough the for the item specified by name.
		 * 
		 * @param name The name of the item to search for
		 * @return returns null if item was not found.
		 */
		//Returns null if item is not found.
		public Item getItem(String name){
			for (Item item : items) {
				if(item.getName() == name){
					return item;
				}
			}
			
			return null;
			
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
		
		
		
		/**
		 * @return The set of items in the inventory
		 */
		public HashSet<Item> getItems(){
			return items;
		}
		
		
	}
	
	
}
