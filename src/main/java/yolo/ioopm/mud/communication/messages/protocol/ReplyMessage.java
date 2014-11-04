package yolo.ioopm.mud.communication.messages.protocol;

import yolo.ioopm.mud.communication.messages.OutgoingMessage;

public class ReplyMessage extends OutgoingMessage {

	
	public ReplyMessage(String reciever, String[] nouns) {
		super(reciever, "server", "echo_reply", System.currentTimeMillis(), nouns);
		// TODO Auto-generated constructor stub
	}

}
