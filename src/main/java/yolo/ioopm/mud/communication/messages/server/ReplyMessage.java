package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;

public class ReplyMessage extends Message {
	public ReplyMessage(String reciever, String action, String[] nouns) {
		super(reciever, "server", action, System.currentTimeMillis(), nouns);
	}
}
