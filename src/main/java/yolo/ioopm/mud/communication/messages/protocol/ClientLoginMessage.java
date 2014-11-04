package yolo.ioopm.mud.communication.messages.protocol;

import yolo.ioopm.mud.communication.messages.IncomingMessage;

public class ClientLoginMessage extends IncomingMessage {
	public ClientLoginMessage(String username, String password) {
		super("server", "unknown", "login", System.currentTimeMillis(), username, password);
	}
}
