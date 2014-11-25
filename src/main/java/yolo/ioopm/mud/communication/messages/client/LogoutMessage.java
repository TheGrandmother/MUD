package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class LogoutMessage extends Message {
	public LogoutMessage(String sender) {
		super("server", sender, MessageType.LOGOUT, null, null);
	}
}
