package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class AuthenticationReplyMessage extends Message {
	public AuthenticationReplyMessage(String receiver, boolean sucessfull) {
		super(receiver, "server", MessageType.AUTHENTICATION_REPLY, null, String.valueOf(sucessfull));
	}
}
