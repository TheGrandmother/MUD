package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * Sent from server to client to reply if the registration attempt was successful.
 */
public class RegistrationReplyMessage extends Message{
	public RegistrationReplyMessage(String receiver, boolean success) {
		super(receiver, "server", MessageType.REGISTRATION_REPLY, null, new String[]{String.valueOf(success)});
	}
}
