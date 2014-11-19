package yolo.ioopm.mud;

import yolo.ioopm.mud.ansi.GeneralAnsiCodes;
import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.AuthenticationMessage;
import yolo.ioopm.mud.communication.messages.client.GeneralActionMessage;
import yolo.ioopm.mud.communication.messages.client.RegistrationMessage;
import yolo.ioopm.mud.game.Keywords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());

	private enum MenuItem {
		LOGIN(1),
		REGISTER(2);

		private final int INDEX;

		private MenuItem(int index) {
			INDEX = index;
		}

		public int getIndex() {
			return INDEX;
		}

		public static MenuItem getFromIndex(int index) {
			for(MenuItem item : values()) {
				if(item.getIndex() == index) {
					return item;
				}
			}

			return null;
		}
	}

	//TODO read these values from user
	private String username = null;
	private String password = null;

	private final BufferedReader keyboard_reader;

	private Adapter adapter = null;

	public Client() {
		logger.fine("Initiating client!");

		keyboard_reader = new BufferedReader(new InputStreamReader(System.in));

		displayWelcomeMessage();

		String address = promptForHostname();
		username = promptForUsername();
		password = promptForPassword();

		if(connect(address)) {
			while(true) {
				switch(showMenu()) {
					case LOGIN:
						if(login()) {
							run();
						}
						break;
					case REGISTER:
						register();
						break;
				}
			}
		}
	}

	/**
	 * Starts to prompt the user for actions etc.
	 */
	private void run() {
		while(true) {
			String action = promptForAction().toLowerCase();

			String[] temp = prompt("Please enter arguments, separated by \",\":").split(",");

			adapter.sendMessage(new GeneralActionMessage(username, action, temp));

			Message msg;
			while((msg = adapter.poll()) == null) {
				try {
					Thread.sleep(100);
				}
				catch(InterruptedException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			System.out.println(msg.getMessage());

//			switch(action) {
//				case Keywords.MOVE:
//					break;
//
//				default:
//					logger.fine("User entered non implemented action! Action: \"" + action + "\"");
//					System.out.println("You entered an incorrect action!");
//					break;
//			}
		}
	}

	/**
	 * Connects to the server at the given host address.
	 * Uses the default port defined in Server.DEFAULT_PORT
	 *
	 * @param host Address to connect to, might be an IP-address or URL.
	 * @return true if connection was established
	 */
	private boolean connect(String host) {
		System.out.println("Connecting to server...");

		int port = Server.DEFAULT_PORT;

		try {
			adapter = new ClientAdapter(host, port, username);
			return true;
		}
		catch(IOException e) {
			logger.log(Level.FINER, "Failed to create ClientAdapter for host \"" + host + "\"!", e);
			System.out.println("Could not connect to server! Please wait...");

			// Wait before printing the menu again.
			try {
				Thread.sleep(2500);
			}
			catch(InterruptedException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}

			return false;
		}
	}

	/**
	 * Outputs ANSI escape codes to the terminal that clears it and resets the cursor to the upper left position.
	 */
	private void clearScreen() {
		System.out.print(GeneralAnsiCodes.CLEAR_SCREEN.toString());
		System.out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1).toString());
	}

	/**
	 * Clears the screen and prints out the welcome message.
	 */
	private void displayWelcomeMessage() {
		clearScreen();
		System.out.println("Welcome to MUD!");
	}

	/**
	 * Attempts to login to the server.
	 * NOTE, a connection has be have been established prior to the call to this method!
	 *
	 * @return true if the log in attempt was successful.
	 */
	private boolean login() {
		clearScreen();

		System.out.println("Attempting to login to server...");

		if(authenticate(username, password)) {
			System.out.println("You successfully authenticated yourself!");
			return true;
		}
		else {
			System.out.println("Failed to authenticate against server! Is the name is use or are the details incorrect?");
			System.out.println("If you have not registered yourself on this server, please do so prior to connecting!");

			// Wait before printing the menu again.
			try {
				Thread.sleep(2500);
			}
			catch(InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}

			return false;
		}
	}

	/**
	 * Attempts to register at the serer.
	 * NOTE, a connection has be have been established prior to the call to this method!
	 *
	 * @return true if the method was successful
	 */
	private boolean register() {
		clearScreen();

		System.out.println("Attempting to register at server...");

		adapter.sendMessage(new RegistrationMessage(username, username, password));

		logger.fine("Waiting for server to reply...");

		while(true) {
			Message answer;
			while((answer = adapter.poll()) == null) {
				try {
					Thread.sleep(200);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(answer.getType() == MessageType.REGISTRATION_REPLY) {
				switch(answer.getArguments()[0]) {
					case "false":
						System.out.println("Registration failed! That username is probably already in use!");
						return false;
					case "true":
						System.out.println("Successfully registered at server! You can now log in!");
						return true;
					default:
						logger.severe("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
						return false;
				}
			}
			else {
				logger.fine("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			}
		}
	}

	/**
	 * Sends an AuthenticationMessage to the server with the given username and password.
	 * NOTE, a connection has be have been established prior to the call to this method!
	 *
	 * @param username
	 * @param password
	 * @return true if server responds with successful
	 */
	private boolean authenticate(String username, String password) {
		// Authenticate against server
		Message msg = new AuthenticationMessage(username, username, password);
		adapter.sendMessage(msg);

		logger.fine("Waiting for server to reply...");

		// Poll adapter every 0.2 seconds until we receive an answer.
		while(true) {
			Message answer;
			while((answer = adapter.poll()) == null) {
				try {
					Thread.sleep(200);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(answer.getType() == MessageType.AUTHENTICATION_REPLY) {
				switch(answer.getArguments()[0]) {
					case "false":
						return false;
					case "true":
						return true;
					default:
						logger.severe("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
						return false;
				}
			}
			else {
				logger.fine("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			}
		}
	}

	/**
	 * Prompts the user for a username.
	 * @return the value entered by the user
	 */
	private String promptForUsername() {
		return prompt("Please enter your username:");
	}

	/**
	 * Prompts the user for password
	 * @return the value entered by the user
	 */
	private String promptForPassword() {
		return prompt("Please enter your password:");
	}

	/**
	 * Prompts the user for a host name
	 * @return the value entered by the user
	 */
	private String promptForHostname() {
		return prompt("Please enter server address:");
	}

	/**
	 * Prompts the user for an action.
	 * @return value entered by user.
	 */
	private String promptForAction() {
		return prompt("Please enter action:");
	}

	/**
	 * Prompts the user with the given question.
	 * @param question
	 * @return value entered by the user
	 */
	private String prompt(String question) {
		System.out.println(question);

		String answer;
		try {
			answer = keyboard_reader.readLine();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		return answer;
	}

	/**
	 * Prints the menu to the terminal and prompts the user for input
	 * @return the MenuItem chosen by the user
	 */
	private MenuItem showMenu() {
		StringBuilder sb = new StringBuilder();

		clearScreen();
		sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(0));
		sb.append("Please enter index number of what you would like to do:");
		sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(2).setIntTwo(0));

		int i = 3;
		for(MenuItem item : MenuItem.values()) {
			sb.append(item.getIndex() + ". " + item.name());
			sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(i++).setIntTwo(0));
		}

		sb.append("Input:");
		sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(i).setIntTwo(0));

		System.out.print(sb.toString());

		String choice;
		try {
			choice = keyboard_reader.readLine();
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}

		int index;
		try {
			index = Integer.valueOf(choice);
		}
		catch(NumberFormatException e) {
			logger.log(Level.WARNING, "Index chosen is not a valid integer!", e);
			return null;
		}

		return MenuItem.getFromIndex(index);
	}
}
