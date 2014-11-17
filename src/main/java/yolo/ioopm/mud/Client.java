package yolo.ioopm.mud;

import yolo.ioopm.mud.ansi.GeneralAnsiCodes;
import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.AuthenticationMessage;
import yolo.ioopm.mud.communication.messages.client.RegistrationMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());

	private boolean is_connected = false;
	private boolean has_crashed = false;

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

		// Retrieve username from user
		username = askForUsername();
		password = askForPassword();

		while(!is_connected) {

			while(adapter == null) {
				connect();
			}

			switch(showMenu()) {
				case LOGIN:
					login();
					break;
				case REGISTER:
					register();
					break;
			}

			if(has_crashed) {
				logger.severe("The client crashed unexpectedly! Please refer to log.");
				return;
			}
		}
	}

	private void connect() {
		System.out.println("Connecting to server...");

		clearScreen();

		String host = askForHostname();
		int port = Server.DEFAULT_PORT;

		try {
			adapter = new ClientAdapter(host, port, username);
		}
		catch(IOException e) {
			logger.log(Level.FINER, "Failed to create ClientAdapter for host \"" + host + "\"!", e);
			System.out.println("Could not connect to server! Please wait...");

			// Wait three seconds before printing the menu again.
			try {
				Thread.sleep(3000);
			}
			catch(InterruptedException e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}

			return;
		}
	}

	private void clearScreen() {
		System.out.print(GeneralAnsiCodes.CLEAR_SCREEN.toString());
		System.out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1).toString());
	}

	private void displayWelcomeMessage() {
		clearScreen();
		System.out.println("Welcome to MUD!");
	}

	private void login() {
		clearScreen();

		System.out.println("Attempting to login to server...");

		if(authenticate(username, password)) {
			System.out.println("You successfully authenticated yourself!");
			is_connected = true;
		}
		else {
			System.out.println("Failed to authenticate against server! Is the name is use or are the details incorrect?");
			System.out.println("If you have not registered yourself on this server, please do so prior to connecting!");

			// Wait three seconds before printing the menu again.
			try {
				Thread.sleep(3000);
			}
			catch(InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	private void register() {
		clearScreen();

		System.out.println("Attempting to register at server...");

		adapter.sendMessage(new RegistrationMessage(username, username, password));

		logger.fine("Waiting for server to reply...");

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
				case "true":
					System.out.println("Successfully registered at server! You can now log in!");
				default:
					logger.severe("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
					has_crashed = true;
			}
		}
		else {
			logger.severe("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			has_crashed = true;
		}
	}

	private boolean authenticate(String username, String password) {
		// Authenticate against server
		Message msg = new AuthenticationMessage(username, password);
		adapter.sendMessage(msg);

		logger.fine("Waiting for server to reply...");

		// Poll adapter every 0.2 seconds until we receive an answer.
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
					has_crashed = true;
					return false;
			}
		}
		else {
			logger.severe("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			has_crashed = true;
			return false;
		}
	}

	private String askForUsername() {
		return ask("Please enter your username:");
	}

	private String askForPassword() {
		return ask("Please enter your password:");
	}

	private String askForHostname() {
		return ask("Please enter hostname:");
	}

	private String ask(String question) {
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

	private MenuItem showMenu() {
		StringBuilder sb = new StringBuilder();

		clearScreen();
		sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(2).setIntTwo(0));
		sb.append("^^^^^^^^^^ INPUT ^^^^^^^^^^");
		sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(3).setIntTwo(0));

		int i = 4;
		for(MenuItem item : MenuItem.values()) {
			sb.append(item.getIndex() + ". " + item.name());
			sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(i++).setIntTwo(0));
		}

		sb.append(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(0));

		System.out.print(sb.toString());

		String choice;
		try {
			choice = keyboard_reader.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		int index;
		try {
			index = Integer.valueOf(choice);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

		return MenuItem.getFromIndex(index);
	}
}
