package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class RegistrationReplyMessage extends Message{
	public RegistrationReplyMessage(String receiver, boolean success) {
		super(receiver, "server", MessageType.REGISTRATION_REPLY, null, new String[]{String.valueOf(success)});
	}
}
