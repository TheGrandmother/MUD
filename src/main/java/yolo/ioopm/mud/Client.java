package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.AuthenticationMessage;
import yolo.ioopm.mud.communication.messages.client.GeneralActionMessage;
import yolo.ioopm.mud.communication.messages.client.RegistrationMessage;
import yolo.ioopm.mud.ui.Action;
import yolo.ioopm.mud.ui.ClientInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private final BufferedReader keyboard_reader;

	private String  host_address = null;
	private String  username     = null;
	private String  password     = null;
	private Adapter adapter      = null;

	public Client() {
		logger.fine("Initiating client!");

		keyboard_reader = new BufferedReader(new InputStreamReader(System.in));

		ClientInterface ui = new ClientInterface(this, keyboard_reader);
		ui.run();
	}

	public void performAction(Action action, String[] arguments) {
		adapter.sendMessage(new GeneralActionMessage(username, action.toString().toLowerCase(), arguments));
	}

	/**
	 * Connects to the host_address at the given host address.
	 * Uses the default port defined in Server.DEFAULT_PORT
	 *
	 * @return true if connection was established
	 */
	public boolean connect() {
		System.out.println("Connecting to server...");

		int port = Server.DEFAULT_PORT;

		try {
			adapter = new ClientAdapter(host_address, port, username);
			return true;
		}
		catch(IOException e) {
			logger.log(Level.FINER, "Failed to create ClientAdapter for host \"" + host_address + "\"!", e);
			System.out.println("Could not connect to host_address! Please wait...");

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
	public Message pollMessage() throws IOException {

		if(adapter == null) {
			throw new IOException("No connection has been established!");
		}

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

		adapter.sendMessage(new RegistrationMessage(username, username, password));

		logger.fine("Waiting for host_address to reply...");

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
						return false;
					case "true":
						return true;
					default:
						logger.severe("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
						throw new IOException("Reply message was illegally formed!");
				}
			}
			else {
				logger.fine("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			}
		}
	}

	/**
	 * Sends an AuthenticationMessage to the host_address with the given username and password.
	 * NOTE, a connection has be have been established prior to the call to this method!
	 *
	 * @return true if host_address responds with successful
	 */
	public boolean authenticate() throws IOException {

		if(adapter == null) {
			throw new IOException("No connection has been established!");
		}

		// Authenticate against host_address
		adapter.sendMessage(new AuthenticationMessage(username, username, password));

		logger.fine("Waiting for host_address to reply...");

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

	public void setUsername(String n) {
		username = n;
	}

	public void setPassword(String p) {
		password = p;
	}

	public void setServerAddress(String host) {
		host_address = host;
	}
}
