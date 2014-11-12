package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class GeneralActionMessage extends Message {
	public GeneralActionMessage(String sender, String action, String[] nouns) {
		super("server", sender, MessageType.GENERAL_ACTION, action, nouns);
	}
}
