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
	 * @param sucessfull Weather or not it was successfull.
	 */
	public AuthenticationReplyMessage(String receiver, boolean sucessfull, String message) {
		super(receiver, "server", MessageType.AUTHENTICATION_REPLY, null, String.valueOf(sucessfull), message);
	}
}
