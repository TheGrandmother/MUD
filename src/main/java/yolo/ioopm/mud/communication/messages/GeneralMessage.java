package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public class GeneralMessage extends Message {

	public GeneralMessage(String receiver, String sender, String... nouns) {
		this(receiver, sender, Action.UNKNOWN, nouns);
	}

	public GeneralMessage(String receiver, String sender, long time_stamp, String... nouns) {
		this(receiver, sender, Action.UNKNOWN, time_stamp, nouns);
	}

	public GeneralMessage(String receiver, String sender, Action action, String... nouns) {
		super(receiver, sender, action, nouns);
	}

	public GeneralMessage(String receiver, String sender, Action action, long time_stamp, String... nouns) {
		super(receiver, sender, action, time_stamp, nouns);
	}
}