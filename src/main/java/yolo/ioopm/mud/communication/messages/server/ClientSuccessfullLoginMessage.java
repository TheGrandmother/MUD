package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;

/**
 * This is sent to the client from the server if the client was successfull at authentication
 */
public class ClientSuccessfullLoginMessage extends Message {
	public ClientSuccessfullLoginMessage(String receiver) {
		super(receiver, "server", "successfulllogin", System.currentTimeMillis(), null);
	}
}
