package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class ErrorMessage extends Message {
	public ErrorMessage(String reciever, String error_message) {
		super(reciever, "server", MessageType.GENERAL_ERROR, null, System.currentTimeMillis(), error_message);
	}
}
