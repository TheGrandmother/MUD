package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public abstract class OutgoingMessage extends Message {

	public OutgoingMessage(String reciever, String sender, String action,
			long time_stamp, String[] nouns) {
		super(reciever, sender, action, time_stamp, nouns);
		// TODO Auto-generated constructor stub
	}

}
