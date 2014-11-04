package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;

public class ClientLoginMessage extends Message {
	public ClientLoginMessage(String username, String password) {
		super("server", "unknown", "login", System.currentTimeMillis(), username, password);
	}
}
