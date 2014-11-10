package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * This is sent to the client from the server if the client was successfull at authentication
 */
public class ClientSuccessfullLoginMessage extends Message {
	public ClientSuccessfullLoginMessage(String receiver) {
		super(receiver, "server", MessageType.AUTHENTICATION_REPLY, null, System.currentTimeMillis(), "successfulllogin");
	}
}
