package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;

public class ReplyMessage extends Message {
	public ReplyMessage(String reciever, String[] nouns) {
		super(reciever, "server", "echo_reply", System.currentTimeMillis(), nouns);
	}
}
