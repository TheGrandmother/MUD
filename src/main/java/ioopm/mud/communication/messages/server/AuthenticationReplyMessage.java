package ioopm.mud.communication.messages.server;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;

/**
 * 
 * This message is a reply to a query from a client to login a player
 * 
 * @author TheGrandmother
 *
 */
public class AuthenticationReplyMessage extends Message {
	/**
	 * Constructs the message.
	 * @param receiver Who requested the authentication
	 * @param successful Weather or not it was successful.
	 */
	public AuthenticationReplyMessage(String receiver, boolean successful, String message) {
		super(receiver, "server", MessageType.AUTHENTICATION_REPLY, null, String.valueOf(successful), message);
	}
}
