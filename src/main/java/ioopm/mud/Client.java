package ioopm.mud;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;
import ioopm.mud.communication.messages.client.*;
import ioopm.mud.communication.websocket.WSClientAdapter;
import ioopm.mud.exceptions.ConnectionRefusalException;
import ioopm.mud.ui.ActionMenu;
import ioopm.mud.ui.ClientInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private final BufferedReader keyboard_reader;

	private String host_address = null;
	private String username = null;
	private String password = null;
	private Adapter adapter = null;

	/**
	 * Constructs a new client with an interface.
	 */
	public Client() throws URISyntaxException {
		logger.fine("Initiating client!");

		keyboard_reader = new BufferedReader(new InputStreamReader(System.in));

		ClientInterface ui = new ClientInterface(this, keyboard_reader);
		ui.run();
	}

	/**
	 * Constructs a message with the given action and arguments and hands it to the adapter
	 * to send to the server.
	 *
	 * @param action    - Action to perform.
	 * @param arguments - Arguments if any to go with the action.
	 */
	public void performAction(ActionMenu action, String[] arguments) {
		adapter.sendMessage(new GeneralActionMessage(username, action.toString().toLowerCase(), arguments));
	}

	/**
	 * Connects to the host_address at the given host address.
	 * Uses the default port defined in Server.DEFAULT_PORT
	 *
	 * @return true if connection was established
	 * @throws IOException                                     - If an I/O error occurred.
	 * @throws ioopm.mud.exceptions.ConnectionRefusalException - If the host refused the connection.
	 */
	public boolean connect() throws IOException, ConnectionRefusalException, URISyntaxException {
		logger.info("Connecting to server...");

		//adapter = new TCPClientAdapter(host_address, port, username);
		WSClientAdapter websockclient = new WSClientAdapter(new URI("ws://" + host_address + ":" + Server.DEFAULT_PORT));
		websockclient.connect();

		// Wait for the connection to be established
		while(!websockclient.isOpen()) {
			try {
				Thread.sleep(100L);
			} catch(InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		// Store the reference to the adapter
		adapter = websockclient;

		// Initiate the MUD protocol
		adapter.sendMessage(new HandshakeMessage(username));

		// Retrieve the server's reply
		Message answer = pollMessage();
		if(answer.getType() == MessageType.HANDSHAKE_REPLY) {
			String[] args = answer.getArguments();

			if(args[0].equals("true")) {
				return true;
			} else {
				throw new ConnectionRefusalException(args[1]);
			}
		} else {
			logger.severe("Server sent a \"" + answer.getType() + "\" message instead of a handshake reply!");
		}

		return false;
	}

	/**
	 * Polls the adapter every 50 milliseconds until a message has been received.
	 * Warning! This is a blocking method!
	 *
	 * @return - First message in message-queue.
	 * @throws IOException - If an I/O error occurred.
	 */
	public Message pollMessage() throws IOException {

		if(adapter == null) {
			throw new IOException("No connection has been established!");
		}

		Message msg;
		while((msg = adapter.poll()) == null) {
			try {
				Thread.sleep(50);
			} catch(InterruptedException e) {
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
	 * @throws IOException                - If an I/O error occurred.
	 * @throws ConnectionRefusalException - If the host refused the connection.
	 */
	public boolean register() throws IOException, ConnectionRefusalException {

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
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(answer.getType() == MessageType.REGISTRATION_REPLY) {

				String[] args = answer.getArguments();

				switch(args[0]) {
					case "false":
						throw new ConnectionRefusalException(args[1]);
					case "true":
						return true;
					default:
						logger.severe("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
						throw new IOException("Reply message was illegally formed!");
				}
			} else {
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
	public boolean authenticate() throws IOException, ConnectionRefusalException {

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
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(answer.getType() == MessageType.AUTHENTICATION_REPLY) {

				String[] args = answer.getArguments();

				switch(args[0]) {
					case "false":
						throw new ConnectionRefusalException(args[1]);
					case "true":
						return true;
					default:
						throw new IOException("Reply message was illegally formed!");
				}
			} else {
				logger.fine("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			}
		}
	}

	/**
	 * Force sends a logout message to the server.
	 */
	public void logout() {
		adapter.sendMessage(new LogoutMessage(username));
	}

	/**
	 * Sets the username for the client.
	 *
	 * @param name - The name to use.
	 */
	public void setUsername(String name) {
		username = name;
	}

	/**
	 * Sets the password for the client.
	 *
	 * @param password - The password to use.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the address to the server which this client should connect to.
	 *
	 * @param host - Host address to server.
	 */
	public void setServerAddress(String host) {
		host_address = host;
	}
}
