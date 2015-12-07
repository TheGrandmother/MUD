package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;

/**
 * Sent from client to server when the client logs out.
 */
public class LogoutMessage extends Message {

	/**
	 * Constructs the message.
	 * @param sender Name of the sender.
	 */
	public LogoutMessage(String sender) {
		super("server", sender, MessageType.LOGOUT, null);
	}
}
