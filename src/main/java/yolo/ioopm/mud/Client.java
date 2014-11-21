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
import yolo.ioopm.mud.userinterface.ClientInterface;

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

		username = "player2";
		password = "123";
		connect("192.168.1.102");

		ClientInterface ui = new ClientInterface(this, System.out, keyboard_reader);
		ui.run();
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
	 * Polls the adapter until a message has been received.
	 * Warning! This is a blocking method!
	 *
	 * @return - First message in message-queue.
	 */
	public Message pollMessage() {

		Message msg;
		while((msg = adapter.poll()) == null) {
			try {
				Thread.sleep(50);
			}
			catch(InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		return msg;
	}

	/**
	 * Attempts to register at the serer.
	 * NOTE, a connection has be have been established prior to the call to this method!
	 *
	 * @return true if the method was successful
	 */
	public boolean register() throws IOException {

		if(adapter == null) {
			throw new IOException("No connection has been established!");
		}

//		System.out.println("Attempting to register at server...");

		adapter.sendMessage(new RegistrationMessage(username, username, password));

//		logger.fine("Waiting for server to reply...");

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
//						System.out.println("Registration failed! That username is probably already in use!");
						return false;
					case "true":
//						System.out.println("Successfully registered at server! You can now log in!");
						return true;
					default:
//						logger.severe("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
						throw new IOException("Reply message was illegally formed!");
				}
			}
			else {
//				logger.fine("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
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
	public boolean authenticate() throws IOException {

		if(adapter == null) {
			throw new IOException("No connection has been established!");
		}

		// Authenticate against server
		adapter.sendMessage(new AuthenticationMessage(username, username, password));

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
						throw new IOException("Reply message was illegally formed!");
				}
			}
			else {
				logger.fine("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			}
		}
	}

	/**
	 * Prints the menu to the terminal and prompts the user for input
	 * @return the MenuItem chosen by the user
	 */
	private MenuItem showMenu() {
		StringBuilder sb = new StringBuilder();

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

	public void setUsername(String n) {
		username = n;
	}

	public void setPassword(String p) {
		password = p;
	}
}
