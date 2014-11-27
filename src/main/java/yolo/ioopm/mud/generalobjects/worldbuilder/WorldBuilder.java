package yolo.ioopm.mud.generalobjects.worldbuilder;

import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.World.EntityNotUnique;
import yolo.ioopm.mud.generalobjects.items.Key;
import yolo.ioopm.mud.generalobjects.items.Weapon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class WorldBuilder {

	
	private final File item_file;
	private final File room_file;
	//the world to be built!
	private World world;
	//these to maps are needed since we can only add exits and items that already exist!
	/**
	 * Pairs are on the form name_of_room, {exit_name;locked} where locked is either "true" or "false". 
	 */
	private HashMap<String, String[]> exit_list= new HashMap<>();
	/**
	 * Pairs are on the form name_of_room, {item_name;amount} where amount is 
	 * an int corresponding to the number of items in the room..
	 */
	private HashMap<String, String[]> item_list= new HashMap<>();
	/**
	 * List containing all of the lobbies in the room with entrys on the form{roomname;level}
	 */
	private ArrayList<String> lobby_list= new ArrayList<>();
	
	public WorldBuilder(String item_file_name, String room_file_name) {
		item_file = new File(item_file_name);
		room_file = new File(room_file_name);
		
	}
		
	public World buildWorld(World world){
		this.world = world;
		try {
			parseItems();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			System.out.println("SYNTAX ERROR in item file: "+e.getReason());
			System.exit(0);
		}
		
		try {
			parseRoom();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			System.out.println("SYNTAX ERROR in room file: "+e.getReason());
			System.exit(0);
		}
		
		System.out.println("attempting to add lobbys");
		for (String s : lobby_list) {
			try {
				world.addLobby(s.split(";")[0].trim(), Integer.parseInt(s.split(";")[1].trim()));
			} catch (NumberFormatException e) {
				System.out.println("SYNTAX ERROR when adding lobby: malformed argument " + s);
				System.exit(0);
			} catch (EntityNotPresent e) {
				System.out.println("SYNTAX ERROR when adding lobby: Non exisitng room " + s);
				System.exit(0);
			}catch (ArrayIndexOutOfBoundsException e){
				System.out.println("SYNTAX ERROR when adding lobby: malformed argument " + s);
				System.exit(0);
			} catch (EntityNotUnique e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		
		return world;
	}
	
	
	
	
	private void parseItems() throws IOException, SyntaxError{

		String current_line;
		int line_counter = 0;
		BufferedReader reader = new BufferedReader(new FileReader(item_file));
		
		current_line = reader.readLine();
		while(current_line != null){
			line_counter++;
			if(!current_line.equals("") && !current_line.startsWith("/")){
				String[] args = current_line.trim().split(":");
				switch (args[0].trim()) {
				case "weapon":
					if(args.length != 6){
						throw new SyntaxError("Wrong number of arguments for weapon!",line_counter);
					}else{
						try {
							world.addItem(new Weapon(args[1].trim(), args[2].trim(), Integer.parseInt(args[3].trim()), Integer.parseInt(args[4].trim()), Integer.parseInt(args[5].trim())));
						} catch (NumberFormatException e) {
							throw new SyntaxError("Malformed argument", line_counter,current_line);
						} catch (EntityNotUnique e) {
							throw new SyntaxError("Not a unique name", line_counter,current_line);
						}
					}
					break;
				case "key":
					if(args.length != 3){
						throw new SyntaxError("Wrong number of arguments for weapon!",line_counter,current_line);
					}else{
						try {
							world.addItem(new Key(args[1].trim(), Integer.parseInt(args[2].trim())));
						} catch (NumberFormatException e) {
							throw new SyntaxError("Malformed argument", line_counter,current_line);
						} catch (EntityNotUnique e) {
							throw new SyntaxError("Not a unique name", line_counter,current_line);
						}
					}
					break;

				default:
					throw new SyntaxError("Unrecognised type!",line_counter,current_line);
					
				}
			}
			
			current_line = reader.readLine();
		}
		System.out.println("item file parsed without issues");
		reader.close();
	}
	
	private void parseRoom() throws IOException, SyntaxError{
		boolean has_lobby =false;
		String current_line;
		int line_counter = 1;
		BufferedReader reader = new BufferedReader(new FileReader(room_file));
		
		current_line = reader.readLine();
		while(current_line != null){
			if(!current_line.equals("") && !current_line.startsWith("/")){
				String[] args = current_line.trim().split(":");
				if(args[0].trim().equals("room")){
				try {
					world.addRoom(new Room(args[1].trim(),args[2].trim(),Boolean.parseBoolean(args[3].trim())));
					if(!args[4].trim().equals("none")){
						item_list.put(args[1].trim(), args[4].trim().split(","));
					}
					if(args[5].equals("none")){
						throw new SyntaxError("Dude a room with no exits is a pointless room!", line_counter,current_line);
					}else{
						exit_list.put(args[1].trim(), args[5].trim().split(","));
					}
				} catch (EntityNotUnique e) {
					reader.close();
					throw new SyntaxError("Room "+ args[1].trim() + " has allready been added :P", line_counter,current_line);
					
				}
				}else if(args[0].trim().equals("lobby")){
					if(!has_lobby){
						for(String s : args[1].split(",")){
							lobby_list.add(s.trim());
						}
						has_lobby =true;
						
					}else{
						reader.close();
						throw new SyntaxError("the room can only contain a lobby file!", line_counter, current_line);
					}
					
				}else{
					reader.close();
					throw new SyntaxError("dude... a roomdeclaration needs to start with room.", line_counter,current_line);
				}
			}
			line_counter++;
			current_line = reader.readLine();
		}
		
		if(!has_lobby){
			reader.close();
			throw new SyntaxError("The room file must specify a lobby list!", -1);
			
		}
		
		System.out.println("World file parsed without issues");
		reader.close();
		System.out.println("Adding exits and items to rooms.");
		
		if(!item_list.isEmpty()){
			for(Entry<String, String[]> s : item_list.entrySet()){
				for(String item : s.getValue()){

					String name = item.trim().split(";")[0].trim() ;
					String amount = item.trim().split(";")[1].trim() ;
					try {
						world.findRoom(s.getKey().trim()).addItem(world.findItem(name),Integer.parseInt(amount));
					} catch (NumberFormatException e) {
						throw new SyntaxError("Got odd number when adding item: " + name + " , " + amount+".", -1);
					} catch (EntityNotPresent e) {
						throw new SyntaxError("Tried to add none exsisting item: " + name+".",-1);
					}
				}
			}
		}
		
		for(Entry<String, String[]> s :exit_list.entrySet()){
			for(String room : s.getValue()){
				String name = room.trim().split(";")[0].trim() ;
				String locked = room.trim().split(";")[1].trim() ;
				try {
					world.findRoom(s.getKey().trim()).addExit(world.findRoom(name),Boolean.parseBoolean(locked));
				} catch (NumberFormatException e) {
					throw new SyntaxError("Got odd boolean when adding exit: " + name + " , " + locked+".", -1);
				} catch (EntityNotPresent e) {
					throw new SyntaxError("Tried to add none exsisting exit: " + name+".",-1);
				}
			}
		}
		
		
		
	}
	
	
	
	
	@SuppressWarnings("serial")
	class SyntaxError extends Exception{
		String silly;
		public String getReason() {
			return silly;
		}
		public SyntaxError(String silly,int line_number, String line_contents){
			this.silly = "At line " + line_number + ". "+silly+"\n"+line_contents;
		}
		public SyntaxError(String silly,int line_number){
			this.silly = "At line " + line_number + ". "+silly;
		}
	}
	
}
