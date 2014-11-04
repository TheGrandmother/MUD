package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;

/**
 * This message is sent from client to server when a client wants to authenticate itself.
 */
public class ClientLoginMessage extends Message {
	public ClientLoginMessage(String username, String password) {
		super("server", "unknown", "login", System.currentTimeMillis(), username, password);
	}
}
