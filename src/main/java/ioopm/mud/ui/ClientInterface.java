package ioopm.mud.ui;

import ioopm.mud.Client;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.exceptions.ConnectionRefusalException;
import ioopm.mud.ui.ansi.AnsiCodes;
import ioopm.mud.ui.ansi.AnsiAttributeCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientInterface {

	private static final Logger logger = Logger.getLogger(ClientInterface.class.getName());

	private final Client         client;
	private final BufferedReader in;

	/**
	 * Constructs a the interface for the client side of the program.
	 * @param instance - The client instance to work against.
	 * @param in - Defines where the interface is reading the input.
	 */
	public ClientInterface(Client instance, BufferedReader in) {
		this.client = instance;
		this.in = in;
	}

	/**
	 * Initiates the interface.
	 */
	public void run() {

		// Prepare the terminal
		formatTerminal();

		// Connect to server
		while(true) {
			client.setServerAddress(prompt("Please enter server address:"));
			client.setUsername(prompt("Please enter username:"));

			try {
				if(client.connect()) {
					logger.info("Connected to server!");
					break;
				}
				else {
					logger.info("Could not connect to specified address!");
					logger.info("Please try again!");
				}
			}
			catch(IOException e) {
				logger.warning("Failed to connect to given server address!");
			}
			catch(ConnectionRefusalException e) {
				logger.warning(e.getMessage());
			}
		}

		// Prompt for password
		client.setPassword(prompt("Please enter password:"));

		// Print initial connection menu
		connectionMenu();

		// Start new thread that prints data from the adapter to the terminal
		startMessageReader();

		// Initiate main action menu
		actionMenu();
	}

	/**
	 * Prints the first connection menu.
	 */
	private void connectionMenu() {
		boolean connected = false;
		while(!connected) {

			// Show the initial menu
			ConnectionMenu menu;
			while(true) {
				String input = prompt(getMenuQuestion(ConnectionMenu.class));

				try {
					menu = ConnectionMenu.valueOf(input.toUpperCase());
					break;
				}
				catch(IllegalArgumentException e) {
					logger.info("Incorrect choice! Please try again!");
				}
			}

			switch(menu) {
				case LOGIN:
					try {
						connected = client.authenticate();
					}
					catch(IOException e) {
						logger.log(Level.FINE, e.getMessage(), e);
						logger.warning("You are not connected to any server!");
						break;
					}
					catch(ConnectionRefusalException e) {
						logger.warning(e.getMessage());
						break;
					}
					logger.info("You are now logged in to the server!");
					break;

				case REGISTER:
					try {
						connected = client.register();
					}
					catch(IOException e) {
						logger.log(Level.FINE, e.getMessage(), e);
						logger.warning("You are not connected to any server!");
						break;
					}
					catch(ConnectionRefusalException e) {
						logger.warning(e.getMessage());
						break;
					}
					logger.info("You have successfully registered at the server!");
					break;
			}
		}
	}

	/**
	 * Prints the main action menu.
	 */
	private void actionMenu() {
		while(true) {
			String input = prompt(getMenuQuestion(ActionMenu.class));

			int space_index = input.indexOf(" ");

			ActionMenu action;
			try {
				action = ActionMenu.valueOf(input.substring(0, (space_index == -1 ? input.length() : space_index)).toUpperCase());
			}
			catch(IllegalArgumentException e) {
				logger.info("Incorrect choice! Please try again!");
				continue;
			}

			String[] args;

			if(space_index != -1) {
				String substring = input.substring(space_index + 1, input.length());
				if(substring.length() == 0) {
					args = null;
				}
				else {
					args = new String[]{substring};
				}
			}
			else {
				args = null;
			}

			switch(action) {

				case MOVE:
					if(args == null) {
						args = new String[]{prompt("What room would you like to move too?")};
					}
					break;

				case SAY:
					if(args == null) {
						args = new String[]{prompt("What would you like to say?")};
					}
					break;

				case ATTACK:
					if(args == null) {
						args = new String[]{prompt("Please enter player to attack:")};
					}
					break;

				case DROP:
					if(args == null) {
						args = new String[]{prompt("What would you like to drop?")};
					}
					break;

				case EQUIP:
					if(args == null) {
						args = new String[]{prompt("What would you like to equip?")};
					}
					break;
				case EXAMINE:
					if(args == null) {
						args = new String[]{prompt("What would you like to examine?")};
					}
					break;

				case INVENTORY:
					break;

				case LOOK:
					break;
				case CS:
					break;

				case TAKE:
					if(args == null) {
						args = new String[]{prompt("What do you want to take?")};
					}
					break;

				case UNEQUIP:
					break;

				case WHISPER:
					args = new String[]{prompt("Who do you want to whisper too?"), prompt("What do you want to whisper?")};
					break;

				case QUIT:
					client.logout();
					System.out.print(AnsiCodes.RESET_SETTINGS);
					System.exit(0);
					break;

				default:
					logger.info("Unimplemented action!");
					return;
			}

			client.performAction(action, args);
		}
	}

	/**
	 * Initiates an async thread that reads new messages from the client and outputs them to the "chat".
	 */
	private void startMessageReader() {
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					while(true) {

						Message msg;
						try {
							logger.fine("MessageReader waiting for message...");
							msg = client.pollMessage();
							logger.fine("MessageReader popped new message! Msg: \"" + msg.getMessage() + "\"");
						}
						catch(IOException e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
							logger.severe("Terminating thread!");
							return;
						}

						logger.fine("MessageReader should now print...");
						logger.info(formatMessage(msg));
					}
				}
			}, "MessageReader"
		).start();
	}

	/**
	 * Outputs the necessary ANSI VT100 escape codes to System.out to format the terminal for usage.
	 */
	private void formatTerminal() {
		System.out.print(AnsiCodes.CLEAR_SCREEN);
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1));
		System.out.print("Welcome to SOUFAD! (the Scary University Of Fear And Doom) \n WARNING: All passwords are stored,logged and sent in PLAIN TEXT!");
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(16).setIntTwo(1));
		System.out.print(AnsiAttributeCodes.WHITE_BACKGROUND_BLACK_TEXT);
		System.out.print("What would you like to do?");
		System.out.print(AnsiAttributeCodes.RESET_ATTRIBUTES);
		System.out.print(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(18).setIntTwo(1));
	}

	/**
	 * Prints the question to the menu bar and waits for the user to input a new line of data.
	 * @param question - The question to print.
	 * @return - The data entered by the user.
	 */
	public String prompt(String question) {

		printToPrompt(question);

		String input;
		try {
			input = in.readLine();
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}

		printToPrompt("Please wait...");

		return input.trim();
	}

	/**
	 * Prints the given string to the menu bar.
	 * @param output - String to print.
	 */
	private void printToPrompt(String output) {
		System.out.print(AnsiCodes.CURSOR_STORE_POSITION);
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(17).setIntTwo(1));
		System.out.print(AnsiCodes.CLEAR_LINE);
		System.out.print(output);
		System.out.print(AnsiCodes.CURSOR_RESTORE_POSITION);
	}

	/**
	 * Formats the given message into a nicer looking string that can be printed to the "chat".
	 * @param msg - The message to format.
	 * @return - The nicer looking string.
	 */
	private String formatMessage(Message msg) {

		logger.fine("Attempting to format message \"" + msg.getMessage() + "\"");

		String action = msg.getAction();
		String[] args = msg.getArguments();

		String retval;

		logger.fine("Initiating main switch-statement...");

		switch(msg.getType()) {
			case GENERAL_ERROR:
				retval = "\u001B[31m" + "ERROR! " + args[0] + "\u001B[39m";
				break;

			case GENERAL_REPLY:
				logger.fine("Initiating General_reply switch");

				switch(action) {
					case "echo_reply":
					case "take_reply":
					case "attack_reply":
						retval = args[0];
						break;

					case "say":
						retval = "\u001B[36m\u001B[1m" + args[0] + ": \u001B[39m\u001B[22m" + args[1];
						break;

					case "whisper_return":
						retval = "\u001B[34m\u001B[1m" + args[0] + ": \u001B[39m\u001B[22m" + args[1];
						break;

					case "inventory_reply":
						if(args.length == 2) {
							retval = "Inventory: " + args[0] + "/" + args[1] + " space left. No items in bag.";
						}
						else if(args.length == 3) {
							retval = "Inventory: " + args[0] + "/" + args[1] + " space left. Items: " + args[2];
						}
						else {
							logger.severe("Incorrect argument length in inventory_reply!");
							retval = "Incorrect argument length in inventory_reply!";
						}
						break;

					case "look_reply":
						retval = "--- " + args[0] + " ---" + "\n" +
							"Description: " + args[1] + "\n" +
							"Exits: " + args[2] + "\n" +
							"Players: " + args[3] + "\n" +
							"NPCs: " + args[4] + "\n" +
							"Items: " + args[5];
						break;

					case "move_reply":
					case "cs_reply":
					case "equip_reply":
					case "unequip_reply":
					case "drop_reply":
					case "examine_reply":
						retval = args[0];
						break;

					default:
						logger.severe("Unsupported message reply! Action: \"" + action + "\"");
						retval = msg.getMessage();
						break;
				}

				logger.fine("General reply switch finished");

				break;

			case SERIOUS_ERROR:
				retval = "\u001B[31m\u001B[1m" + "SERIOUS ERROR! " + args[0] + "\u001B[39m\u001B[22m";
				break;

			case NOTIFICATION:
				retval = "\u001B[35mNotice: " + args[0] + "\u001B[39m";
				break;

			default:
				logger.severe("Unsupported message type! Type: \"" + msg.getType() + "\"");
				retval = msg.getMessage();
				break;
		}

		logger.fine("Returning value \"" + retval + "\"");

		return retval;
	}

	/**
	 * Iterates over the names of all constants in the given enum and constructs a new string from the names.
	 * Example: The ConnectionMenu enum would give the following string; "LOGIN REGISTER"
	 * @param enumClass - The enum to iterate over.
	 * @return - The string of all constant names.
	 */
	private <E extends Enum<E>> String getMenuQuestion(Class<E> enumClass) {
		StringBuilder sb = new StringBuilder();
		for(Enum<E> i : enumClass.getEnumConstants()) {
			sb.append(i.toString()).append(" ");
		}
		return sb.toString();
	}
}
