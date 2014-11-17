package yolo.ioopm.mud;

import yolo.ioopm.mud.ansi.GeneralAnsiCodes;
import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.AuthenticationMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());

	private enum MenuItem {
		CONNECT(1);

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

		run();
	}

	private void run() {
		logger.fine("Run() has been called!");

		displayWelcomeMessage();

		// Retrieve username from user
		username = askForUsername();
		password = askForPassword();

		while(true) {
			switch(showMenu()) {
				case CONNECT:
					connect();
					break;
			}
		}
	}

	private void clearScreen() {
		System.out.println(GeneralAnsiCodes.CLEAR_SCREEN.toString());
		System.out.println(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1).toString());
	}

	private void displayWelcomeMessage() {
		clearScreen();
		System.out.println("Welcome to MUD!");
	}

	private void connect() {
		clearScreen();

		String host = askForHostname();
		int port = Server.DEFAULT_PORT;

		try {
			adapter = new ClientAdapter(host, port, username);
		}
		catch(IOException e) {
			logger.severe("Failed to create ClientAdapter!");
			e.printStackTrace();
			return;
		}

		if(authenticate(username, password)) {
			System.out.println("You successfully authenticated yourself!");
		}
		else {
			System.out.println("Failed to authenticate against server! Is the name is use or are the details incorrect?");
			return;
		}
	}

	private boolean authenticate(String username, String password) {
		// Authenticate against server
		Message msg = new AuthenticationMessage(username, password);
		adapter.sendMessage(msg);

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
					return false;
			}
		}
		else {
			logger.severe("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
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

		System.out.println(sb.toString());

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
