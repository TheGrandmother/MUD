package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;

/**
 * This is sent to client from server if the client failed to authenticate
 */
public class ClientIncorrectLoginMessage extends Message {
	public ClientIncorrectLoginMessage(String receiver) {
		super(receiver, "server", "incorrectlogin", System.currentTimeMillis(), null);
	}
}
