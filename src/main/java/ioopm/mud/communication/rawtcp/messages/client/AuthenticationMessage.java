package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

/**
 * This message is sent from client to server when a client wants to authenticate itself.
 */
public class AuthenticationMessage extends Message {
	/**
	 * Constructs the message.
	 * @param sender The sender whom sent the message.
	 * @param username The username to authenticate with.
	 * @param password The password to authenticate with.
	 */
	public AuthenticationMessage(String sender, String username, String password) {
		super("server", sender, MessageType.AUTHENTICATION, null, username, password);
	}
}
