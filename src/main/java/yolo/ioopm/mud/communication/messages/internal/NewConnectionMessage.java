package yolo.ioopm.mud.communication.messages.internal;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class NewConnectionMessage extends Message {
	public NewConnectionMessage(String username) {
		super("server", "server", MessageType.NEW_CONNECTION, null, username);
	}
}
