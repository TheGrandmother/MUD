package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;

/**
 * Sent from client to server when the client wants to register it self.
 */
public class RegistrationMessage extends Message {
	/**
	 * Constructs the message.
	 *
	 * @param sender   Name of the sender.
	 * @param username Username to register.
	 * @param password Password to register with the username.
	 */
	public RegistrationMessage(String sender, String username, String password) {
		super("server", sender, MessageType.REGISTRATION, null, username, password);
	}
}
