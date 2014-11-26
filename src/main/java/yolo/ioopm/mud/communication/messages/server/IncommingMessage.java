package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * This message is only used for testing.
 */
public class IncommingMessage extends Message {
	public IncommingMessage(String receiver, String sender, String action, String[] nouns) {
		super(receiver, sender, MessageType.GENERAL_ACTION, action, nouns);
	}
}
