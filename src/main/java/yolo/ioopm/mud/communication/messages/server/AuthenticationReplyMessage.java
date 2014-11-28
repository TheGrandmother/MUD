package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * 
 * This message is a reply to a query from a client to login a player
 * 
 * @author TheGrandmother
 *
 */
public class AuthenticationReplyMessage extends Message {
	/**
	 * 
	 * @param receiver Who requested the authentication
	 * @param successful Weather or not it was successful.
	 */
	public AuthenticationReplyMessage(String receiver, boolean successful, String message) {
		super(receiver, "server", MessageType.AUTHENTICATION_REPLY, null, String.valueOf(successful), message);
	}
}
