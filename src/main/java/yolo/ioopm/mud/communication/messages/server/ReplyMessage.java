package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * Sent from server to client as a general reply.
 */
public class ReplyMessage extends Message {
	public ReplyMessage(String reciever, String action, String... nouns) {
		super(reciever, "server", MessageType.GENERAL_REPLY, action, nouns);
	}
}
