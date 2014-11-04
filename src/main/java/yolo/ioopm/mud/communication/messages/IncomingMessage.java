package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public class IncomingMessage extends Message {

	public IncomingMessage(String receiver, String sender, String action, long time_stamp, String... nouns) {
		super(receiver, sender, action, time_stamp, nouns);
	}
}