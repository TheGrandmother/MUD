package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;


public class IncommingMessage extends Message {

	public IncommingMessage(String receiver, String sender, String action,
			String[] nouns) {
		super(receiver, sender, action, nouns);
		// TODO Auto-generated constructor stub
	}

}
