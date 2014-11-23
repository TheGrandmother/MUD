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
			new Runnable() {
				@Override
				public void run() {
					while(true) {
						Message msg;
						try {
							msg = client.pollMessage();
						}
						catch(IOException e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
							logger.severe("Terminating thread!");
							return;
						}
						printToOut(formatMessage(msg));
					}
				}
			}
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

				switch(action) {
					case MOVE:
					case SAY:
					case ATTACK:
					case DROP:
					case EQUIP:
					case INVENTORY:
					case LOOK:
					case TAKE:
					case UNEQUIP:
					case WHISPER:
						String[] arguments = prompt("Please enter arguments separated with \",\"").split(",");
						client.performAction(action.toString().toLowerCase(), arguments);
						break;
					case QUIT:
						synchronized(out) {
							out.print(AnsiCodes.RESET_SETTINGS);
						}
						System.exit(0);
						break;
					default:
						printToOut("Unimplemented action!");
				}
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
			sb.append(i.toString() + " ");
		}
		return sb.toString();
	}

	private String formatMessage(Message msg) {

		String action = msg.getAction();
		String[] args = msg.getArguments();

		switch(msg.getType()) {
			case GENERAL_ERROR:
				return "ERROR! " + args[0];
			case GENERAL_REPLY:
				switch(action) {
					case "echo_reply":
					case "take_reply":
					case "attack_reply":
						return args[0];

					case "say_reply":
						return args[0] + " said \"" + args[1] + "\"";

					case "whisper_reply":
						return args[0] + " whispered to you \"" + args[1] + "\"";

					case "inventory_reply":
						String answer = "Inventory: ";
						answer += args[0] + "/" + args[1] + " space left.";
						answer += " Items: " + args[2];
						return answer;

					case "look_reply":
						StringBuilder s = new StringBuilder();

						s.append("--- ").append(args[0]).append(" ---").append("\n");
						s.append("Descripton: ").append(args[1]).append("\n");
						s.append("Exits: ").append(args[2]).append("\n");
						s.append("Players: ").append(args[3]).append("\n");
						s.append("NPCs: ").append(args[4]).append("\n");
						s.append("Items: ").append(args[5]).append("\n");

						return s.toString();

					default:
						logger.severe("Unsupported message reply! Action: \"" + action + "\"");
						return msg.getMessage();
				}
			case SERIOUS_ERROR:
			case NOTIFICATION:
				return msg.getMessage();
			default:
				logger.severe("Unsupported message type! Type: \"" + msg.getType() + "\"");
				return msg.getMessage();
		}
	}

	public void printToOut(String output) {
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

		synchronized(out) {
			out.print(AnsiCodes.CURSOR_STORE_POSITION);
			out.print(AnsiCodes.CURSOR_SET_POSITION.setIntOne(17).setIntTwo(1));
			out.print(AnsiCodes.CLEAR_LINE);
			out.print(question);
			out.print(AnsiCodes.CURSOR_RESTORE_POSITION);
		}

		String input;
		try {
			input = in.readLine();
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}

		return input;
	}
}
