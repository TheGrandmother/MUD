package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

/**
 * Sent from client to server when the client logs out.
 */
public class LogoutMessage extends Message {
	public LogoutMessage(String sender) {
		super("server", sender, MessageType.LOGOUT, null, null);
	}
}
