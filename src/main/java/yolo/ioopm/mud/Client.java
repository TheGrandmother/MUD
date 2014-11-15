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

public class Client {

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
		keyboard_reader = new BufferedReader(new InputStreamReader(System.in));

		run();
	}

	private void run() {

		displayWelcomeMessage();

		// Retrieve username from user
		username = getUsername();
		password = getPassword();

		while(true) {
			switch(retreiveMenuChoice()) {
				case CONNECT:
					connect();
					break;
			}
		}
	}

	private void clearScreen() {
		System.out.println(GeneralAnsiCodes.CLEAR_SCREEN);
		System.out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1));
	}

	private void displayWelcomeMessage() {
		clearScreen();
		System.out.println("Welcome to MUD!");
	}

	private void connect() {
		String host = "localhost";
		int port = 1337;
		try {
			adapter = new ClientAdapter(host, port, username);
		}
		catch(IOException e) {
			System.out.println("Failed to create ClientAdapter!");
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
					System.out.println("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
					return false;
			}
		}
		else {
			System.out.println("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			return false;
		}
	}

	private String getUsername() {
		System.out.println("Please enter your username:");

		String username;
		try {
			username = keyboard_reader.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		return username;
	}

	private String getPassword() {
		System.out.println("Please enter your password:");

		String password;
		try {
			password = keyboard_reader.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		return password;
	}

	private MenuItem retreiveMenuChoice() {
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
