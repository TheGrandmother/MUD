package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;

public class ErrorMessage extends Message {
	public ErrorMessage(String reciever, String error_message) {
		super(reciever, "server", "echo", System.currentTimeMillis(), new String[]{error_message});
	}
}
