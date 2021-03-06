package ioopm.mud.generalobjects.worldbuilder;

import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.exceptions.EntityNotUnique;
import ioopm.mud.generalobjects.Room;
import ioopm.mud.generalobjects.World;
import ioopm.mud.generalobjects.items.Key;
import ioopm.mud.generalobjects.items.Weapon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class is used to generate a world from two files.
 *
 * @author TheGrandmother
 */
public class WorldBuilder {

	/**
	 * File specifying the items
	 */
	private final File item_file;
	/**
	 * File specifying the rooms
	 */
	private final File room_file;
	//the world to be built!
	private World world;
	//these to maps are needed since we can only add exits and items that already exist!
	/**
	 * Pairs are on the form name_of_room, {exit_name;locked} where locked is either "true" or "false".
	 */
	private HashMap<String, String[]> exit_list = new HashMap<>();
	/**
	 * Pairs are on the form name_of_room, {item_name;amount} where amount is
	 * an int corresponding to the number of items in the room..
	 */
	private HashMap<String, String[]> item_list = new HashMap<>();
	/**
	 * List containing all of the lobbies in the room with entries on the form{roomname;level}
	 */
	private ArrayList<String> lobby_list = new ArrayList<>();

	/**
	 * Creates a WorldBuilder object.
	 *
	 * @param item_file_name Path to the file specifying the items
	 * @param room_file_name Path to the file specifying the rooms
	 */
	public WorldBuilder(String item_file_name, String room_file_name) {
		item_file = new File(item_file_name);
		room_file = new File(room_file_name);

	}


	/**
	 * Creates a brand new world
	 *
	 * @return A new world built from the specifications in {@link WorldBuilder#item_file} and {@link WorldBuilder#item_file}
	 * @throws BuilderException If a world could not be constructed.
	 */
	public World buildWorld() throws BuilderException {
		World world = new World();
		buildWorld(world);
		return world;
	}

	/**
	 * Augments a world with the information in {@link WorldBuilder#item_file} and {@link WorldBuilder#item_file}
	 *
	 * @param world World to be altered
	 * @throws BuilderException If the world could not be augmented
	 */
	public void buildWorld(World world) throws BuilderException {
		this.world = world;
		try {
			parseItems();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(SyntaxError e) {
			throw new BuilderException("SYNTAX ERROR in item file: " + e.getReason());
			//System.exit(0);
		}

		try {
			parseRoom();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(SyntaxError e) {
			throw new BuilderException("SYNTAX ERROR in room file: " + e.getReason());

		}

		//System.out.println("attempting to add lobbys");
		for(String s : lobby_list) {
			try {
				world.addLobby(s.split(";")[0].trim(), Integer.parseInt(s.split(";")[1].trim()));
			} catch(NumberFormatException e) {
				throw new BuilderException("SYNTAX ERROR when adding lobby: malformed argument " + s);

			} catch(EntityNotPresent e) {
				throw new BuilderException("SYNTAX ERROR when adding lobby: Non existing room " + s);

			} catch(ArrayIndexOutOfBoundsException e) {
				throw new BuilderException("SYNTAX ERROR when adding lobby: malformed argument " + s);

			} catch(EntityNotUnique e) {
				throw new BuilderException(e.getMessage());

			}
		}

	}


	/**
	 * Parses the {@link WorldBuilder#item_file} and creates items accordingly.
	 *
	 * @throws IOException If the file could not be read
	 * @throws SyntaxError If the file is mallformed.
	 */
	private void parseItems() throws IOException, SyntaxError {

		String current_line;
		int line_counter = 0;
		BufferedReader reader = new BufferedReader(new FileReader(item_file));

		current_line = reader.readLine();
		while(current_line != null) {
			line_counter++;
			if(!current_line.equals("") && !current_line.startsWith("/")) {
				String[] args = current_line.trim().split(":");
				switch(args[0].trim()) {
					case "weapon":
						if(args.length != 6) {
							throw new SyntaxError("Wrong number of arguments for weapon!", line_counter);
						} else {
							try {
								world.addItem(new Weapon(args[1].trim(), args[2].trim(), Integer.parseInt(args[3].trim()), Integer.parseInt(args[4].trim()), Integer.parseInt(args[5].trim())));
							} catch(NumberFormatException e) {
								throw new SyntaxError("Malformed argument", line_counter, current_line);
							} catch(EntityNotUnique e) {
								throw new SyntaxError("Not a unique name", line_counter, current_line);
							}
						}
						break;
					case "key":
						if(args.length != 3) {
							throw new SyntaxError("Wrong number of arguments for weapon!", line_counter, current_line);
						} else {
							try {
								world.addItem(new Key(args[1].trim(), Integer.parseInt(args[2].trim())));
							} catch(NumberFormatException e) {
								throw new SyntaxError("Malformed argument", line_counter, current_line);
							} catch(EntityNotUnique e) {
								throw new SyntaxError("Not a unique name", line_counter, current_line);
							}
						}
						break;

					default:
						throw new SyntaxError("Unrecognised type!", line_counter, current_line);

				}
			}

			current_line = reader.readLine();
		}
		//System.out.println("item file parsed without issues");
		reader.close();
	}

	/**
	 * Parses the {@link WorldBuilder#room_file} and builds the rooms accordingly.
	 *
	 * @throws IOException If the file could not be read.
	 * @throws SyntaxError If the file is malformed.
	 */
	private void parseRoom() throws IOException, SyntaxError {
		boolean has_lobby = false;
		String current_line;
		int line_counter = 1;
		BufferedReader reader = new BufferedReader(new FileReader(room_file));

		current_line = reader.readLine();
		while(current_line != null) {
			if(!current_line.equals("") && !current_line.startsWith("/")) {
				String[] args = current_line.trim().split(":");
				if(args[0].trim().equals("room")) {
					try {
						world.addRoom(new Room(args[1].trim(), args[2].trim(), Boolean.parseBoolean(args[3].trim())));
						if(!args[4].trim().equals("none")) {
							item_list.put(args[1].trim(), args[4].trim().split(","));
						}
						if(args[5].equals("none")) {
							throw new SyntaxError("Dude a room with no exits is a pointless room!", line_counter, current_line);
						} else {
							exit_list.put(args[1].trim(), args[5].trim().split(","));
						}
					} catch(EntityNotUnique e) {
						reader.close();
						throw new SyntaxError("Room " + args[1].trim() + " has already been added :P", line_counter, current_line);

					}
				} else if(args[0].trim().equals("lobby")) {
					if(!has_lobby) {
						for(String s : args[1].split(",")) {
							lobby_list.add(s.trim());
						}
						has_lobby = true;

					} else {
						reader.close();
						throw new SyntaxError("the room can only contain a lobby file!", line_counter, current_line);
					}

				} else {
					reader.close();
					throw new SyntaxError("dude... a room declaration needs to start with room.", line_counter, current_line);
				}
			}
			line_counter++;
			current_line = reader.readLine();
		}

		if(!has_lobby) {
			reader.close();
			throw new SyntaxError("The room file must specify a lobby list!", -1);

		}

		//System.out.println("World file parsed without issues");
		reader.close();
		//System.out.println("Adding exits and items to rooms.");

		if(!item_list.isEmpty()) {
			for(Entry<String, String[]> s : item_list.entrySet()) {
				for(String item : s.getValue()) {

					String name = item.trim().split(";")[0].trim();
					String amount = item.trim().split(";")[1].trim();
					try {
						world.findRoom(s.getKey().trim()).addItem(world.findItem(name), Integer.parseInt(amount));
					} catch(NumberFormatException e) {
						throw new SyntaxError("Got odd number when adding item: " + name + " , " + amount + ".", -1);
					} catch(EntityNotPresent e) {
						throw new SyntaxError("Tried to add none existing item: " + name + ".", -1);
					}
				}
			}
		}

		for(Entry<String, String[]> s : exit_list.entrySet()) {
			for(String room : s.getValue()) {
				String name = room.trim().split(";")[0].trim();
				String locked = room.trim().split(";")[1].trim();
				try {
					world.findRoom(s.getKey().trim()).addExit(world.findRoom(name), Boolean.parseBoolean(locked));
				} catch(NumberFormatException e) {
					throw new SyntaxError("Got odd boolean when adding exit: " + name + " , " + locked + ".", -1);
				} catch(EntityNotPresent e) {
					throw new SyntaxError("Tried to add none existing exit: " + name + ".", -1);
				}
			}
		}


	}

	/**
	 * Gets thrown when the builder encounters an error.
	 *
	 * @author heso8370
	 */
	@SuppressWarnings("serial")
	public class BuilderException extends Exception {
		/**
		 * Creates a new BuilderException object
		 *
		 * @param message Message describing what went wrong.
		 */
		public BuilderException(String message) {
			super(message);
		}
	}

	/**
	 * This exception is used when the world builder encounters a syntactical error in one of the files.
	 *
	 * @author heso8370
	 */
	@SuppressWarnings("serial")
	class SyntaxError extends Exception {
		String silly;

		/**
		 * Creates  a new SyntaxError with a message containing both what was wrong, at which line and what was written on that
		 * line.
		 *
		 * @param silly         What the error was
		 * @param line_number   At which line the error was encountered
		 * @param line_contents The contents of that line.
		 */
		public SyntaxError(String silly, int line_number, String line_contents) {
			this.silly = "At line " + line_number + ". " + silly + "\n" + line_contents;
		}

		public SyntaxError(String silly, int line_number) {
			this.silly = "At line " + line_number + ". " + silly;
		}

		/**
		 * Returns the reason for the syntax error.
		 *
		 * @return
		 */
		public String getReason() {
			return silly;
		}
	}

}
