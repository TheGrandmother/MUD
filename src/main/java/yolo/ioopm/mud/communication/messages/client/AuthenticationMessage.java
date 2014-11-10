package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * This message is sent from client to server when a client wants to authenticate itself.
 */
public class AuthenticationMessage extends Message {
	public AuthenticationMessage(String username, String password) {
		super("server", "unknown", MessageType.AUTHENTICATION, null, System.currentTimeMillis(), username, password);
	}
}
