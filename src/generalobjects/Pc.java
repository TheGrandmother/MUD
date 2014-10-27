package generalobjects;

import java.util.HashSet;

/**
 * 
 *	This class specifies a PC.
 *
 *	See specifications for an in depth description.
 * 
 * @author heso8370
 *
 */


public class Pc extends Entity{
	
	private String name;
	private Room location;
	private Inventory inventory;
	private CharacterSheet cs;
	
	public Pc(String name, Room starting_location){
		this.name = name;
		location = starting_location;
		inventory = new Inventory();
		cs = new CharacterSheet();
		
		
		
		
		
	}
	
	
	
	class Inventory{
		
		HashSet<Item> items;
		private int volume;
		private int max_volume = 10;
		
		public Inventory(){
			volume = 0;
			items = new HashSet<Item>();
		}
		
		
	}
	
	class CharacterSheet{
		private int hp = 60;
		private int health = 100;
		private int max_health =100;
		private int level = 1;
		
		public CharacterSheet(){
			
		}
	
	}
	
}
