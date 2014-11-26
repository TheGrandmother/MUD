package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * Sent from server to client when the server replies whether the authentication attempt was successful or not.
 */
public class AuthenticationReplyMessage extends Message {
	public AuthenticationReplyMessage(String receiver, boolean sucessfull) {
		super(receiver, "server", MessageType.AUTHENTICATION_REPLY, null, new String[]{String.valueOf(sucessfull)});
	}
}
