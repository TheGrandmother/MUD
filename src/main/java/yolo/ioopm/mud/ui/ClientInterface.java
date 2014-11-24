package yolo.ioopm.mud.ui;

import yolo.ioopm.mud.Client;
import yolo.ioopm.mud.ui.ansi.AnsiColorCodes;
import yolo.ioopm.mud.ui.ansi.AnsiCodes;
import yolo.ioopm.mud.communication.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientInterface {

	private static final Logger logger = Logger.getLogger(ClientInterface.class.getName());

	private final Client client;

	private final PrintStream    out;
	private final BufferedReader in;

	public ClientInterface(Client instance, PrintStream out, BufferedReader in) {
		this.client = instance;
		this.out = out;
		this.in = in;
	}

	public void run() {

		// Prepare the terminal
		formatTerminal();

		while(true) {
			client.setServerAddress(prompt("Please enter server address:"));
			client.setUsername(prompt("Please enter username:"));

			if(client.connect()) {
				printToOut("Connected to server!");
				break;
			}
			else {
				printToOut("Could not connect to specified address!");
				printToOut("Please try again!");
			}
		}

		client.setPassword(prompt("Please enter password:"));

		// Show the initial menu
		MenuItem menu;
		while(true) {
			String input = prompt(getMenuQuestion(MenuItem.class));

			try {
				menu = MenuItem.valueOf(input.toUpperCase());
				break;
			}
			catch(IllegalArgumentException e) {
				printToOut("Incorrect choice! Please try again!");
			}
		}

		boolean connected = false;
		switch(menu) {
			case LOGIN:
				try {
					connected = client.authenticate();
				}
				catch(IOException e) {
					logger.log(Level.FINE, e.getMessage(), e);
					printToOut("You are not connected to any server!");
				}
				break;

			case REGISTER:
				try {
					connected = client.register();
				}
				catch(IOException e) {
					logger.log(Level.FINE, e.getMessage(), e);
					printToOut("You are not connected to any server!");
				}
				break;
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
					printToOut(formatMessage(msg));
				}
			}, "MessageReader"
		).start();

		// If we are connected, start showing the action menu
		if(connected) {
			while(true) {
				String input = prompt(getMenuQuestion(Action.class));

				Action action;
				try {
					action = Action.valueOf(input.toUpperCase());
				}
				catch(IllegalArgumentException e) {
					printToOut("Incorrect choice! Please try again!");
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

					case INVENTORY:
						break;

					case LOOK:
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
						synchronized(out) {
							out.print(AnsiCodes.RESET_SETTINGS);
						}
						System.exit(0);
						break;

					default:
						printToOut("Unimplemented action!");
						return;
				}

				client.performAction(action, args);
			}
		}
		else {
			printToOut("Failed to log in to server!");
		}
	}

	private void formatTerminal() {
		synchronized(out) {
			out.print(AnsiCodes.CLEAR_SCREEN);
			out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1));
			out.print("Welcome to MUD!");
			out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(16).setIntTwo(1));
			out.print(AnsiColorCodes.WHITE_BACKGROUND_BLACK_TEXT);
			out.print("What would you like to do?");
			out.print(AnsiColorCodes.RESET_ATTRIBUTES);
			out.print(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
			out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(18).setIntTwo(1));
		}
	}

	// A bit of generics haxxory to generate a string of all enum constants of any given enum
	private <E extends Enum<E>> String getMenuQuestion(Class<E> enumClass) {
		StringBuilder sb = new StringBuilder();
		for(Enum<E> i : enumClass.getEnumConstants()) {
			sb.append(i.toString()).append(" ");
		}
		return sb.toString();
	}

	private String formatMessage(Message msg) {

		logger.fine("Attempting to format message \"" + msg.getMessage() + "\"");

		String action = msg.getAction();
		String[] args = msg.getArguments();

		String retval;

		logger.fine("Initiating main switch-statement...");

		switch(msg.getType()) {
			case GENERAL_ERROR:
				retval = "ERROR! " + args[0];
				break;

			case GENERAL_REPLY:
				logger.fine("Initiating General_reply switch");

				switch(action) {
					case "echo_reply":
					case "take_reply":
					case "attack_reply":
						retval = args[0];
						break;

					case "say_reply":
						retval = args[0] + " said \"" + args[1] + "\"";
						break;

					case "whisper_return = ":
						retval = args[0] + " whispered to you \"" + args[1] + "\"";
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

					default:
						logger.severe("Unsupported message reply! Action: \"" + action + "\"");
						retval = msg.getMessage();
						break;
				}

				logger.fine("General reply switch finished");

				break;

			case SERIOUS_ERROR:
				retval = "SERIOUS ERROR! " + args[0];
				break;

			case NOTIFICATION:
				retval = "Notice: " + args[0];
				break;

			default:
				logger.severe("Unsupported message type! Type: \"" + msg.getType() + "\"");
				retval = msg.getMessage();
				break;
		}

		logger.fine("Returning value \"" + retval + "\"");

		return retval;
	}

	public void printToOut(String output) {

		logger.info("Printing \"" + output + "\" to out!");

		synchronized(out) {
			out.print(AnsiCodes.CURSOR_STORE_POSITION);
			out.print(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(1).setIntTwo(15));

			// Scroll up the buffer 15 lines
			for(int i = 0; i < 15; i++) {
				out.print(AnsiCodes.BUFFER_MOVE_UP_ONE);
			}

			out.print(output + "\n");

			out.print(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
			out.print(AnsiCodes.CURSOR_RESTORE_POSITION);
		}
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

		return input;
	}

	private void printToPrompt(String output) {
		synchronized(out) {
			out.print(AnsiCodes.CURSOR_STORE_POSITION);
			out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(17).setIntTwo(1));
			out.print(AnsiCodes.CLEAR_LINE);
			out.print(output);
			out.print(AnsiCodes.CURSOR_RESTORE_POSITION);
		}
	}
}