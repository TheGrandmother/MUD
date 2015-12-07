package ioopm.mud.communication.rawtcp.messages.client;

import ioopm.mud.communication.rawtcp.Message;
import ioopm.mud.communication.rawtcp.MessageType;

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
