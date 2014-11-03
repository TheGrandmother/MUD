package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public abstract class OutgoingMessage extends Message {

	public OutgoingMessage(String receiver, String sender, String action, long time_stamp, String[] nouns) {
		super(receiver, sender, action, time_stamp, nouns);
	}
}
