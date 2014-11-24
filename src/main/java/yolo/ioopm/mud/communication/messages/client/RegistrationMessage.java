package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class RegistrationMessage extends Message {
	public RegistrationMessage(String sender, String username, String password) {
		super("server", sender, MessageType.REGISTRATION, null, new String[]{username, password});
	}
}
