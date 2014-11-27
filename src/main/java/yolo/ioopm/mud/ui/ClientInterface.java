package yolo.ioopm.mud.ui;

import yolo.ioopm.mud.Client;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.exceptions.ConnectionRefusalException;
import yolo.ioopm.mud.ui.ansi.AnsiCodes;
import yolo.ioopm.mud.ui.ansi.AnsiColorCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientInterface {

	private static final Logger logger = Logger.getLogger(ClientInterface.class.getName());

	private final Client client;

	private final BufferedReader in;

	public ClientInterface(Client instance, BufferedReader in) {
		this.client = instance;
		this.in = in;
	}

	public void run() {

		// Prepare the terminal
		formatTerminal();

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

		client.setPassword(prompt("Please enter password:"));

		boolean connected = false;
		while(!connected) {

			// Show the initial menu
			MenuItem menu;
			while(true) {
				String input = prompt(getMenuQuestion(MenuItem.class));

				try {
					menu = MenuItem.valueOf(input.toUpperCase());
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

		// Start new thread that prints data from the adapter to the terminal
		new Thread(
			() -> {
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
			}, "MessageReader"
		).start();

		// Action menu
		while(true) {
			String input = prompt(getMenuQuestion(Action.class));

			Action action;
			try {
				action = Action.valueOf(input.toUpperCase());
			}
			catch(IllegalArgumentException e) {
				logger.info("Incorrect choice! Please try again!");
				continue;
			}

			String[] args = null;
			switch(action) {

				case MOVE:
					args = new String[]{prompt("What room would you like to move too?")};
					break;

				case SAY:
					args = new String[]{prompt("What would you like to say?")};
					break;

				case ATTACK:
					args = new String[]{prompt("Please enter player to attack:")};
					break;

				case DROP:
					args = new String[]{prompt("What would you like to drop?")};
					break;

				case EQUIP:
					args = new String[]{prompt("What would you like to equip?")};
					break;
				case EXAMINE:
					args = new String[]{prompt("What would you like to examine?")};
					break;

				case INVENTORY:
					break;

				case LOOK:
					break;
				case CS:
					break;

				case TAKE:
					args = new String[]{prompt("What do you want to take?")};
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

	private void formatTerminal() {
		System.out.print(AnsiCodes.CLEAR_SCREEN);
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1));
		System.out.print("Welcome to MUD!");
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(16).setIntTwo(1));
		System.out.print(AnsiColorCodes.WHITE_BACKGROUND_BLACK_TEXT);
		System.out.print("What would you like to do?");
		System.out.print(AnsiColorCodes.RESET_ATTRIBUTES);
		System.out.print(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(18).setIntTwo(1));
	}

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

	private void printToPrompt(String output) {
		System.out.print(AnsiCodes.CURSOR_STORE_POSITION);
		System.out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(17).setIntTwo(1));
		System.out.print(AnsiCodes.CLEAR_LINE);
		System.out.print(output);
		System.out.print(AnsiCodes.CURSOR_RESTORE_POSITION);
	}

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
						retval = args[0];
						break;
					case "cs_reply":
						retval = args[0];
						break;
					case "equip_reply":
						retval = args[0];
						break;
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

	// A bit of generics haxxory to generate a string of all enum constants of any given enum
	private <E extends Enum<E>> String getMenuQuestion(Class<E> enumClass) {
		StringBuilder sb = new StringBuilder();
		for(Enum<E> i : enumClass.getEnumConstants()) {
			sb.append(i.toString()).append(" ");
		}
		return sb.toString();
	}
}
