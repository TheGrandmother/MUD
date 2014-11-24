package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class SeriousErrorMessage extends Message {

	public SeriousErrorMessage(String receiver, String message) {
		super(receiver, "server", MessageType.SERIOUS_ERROR, null, new String[]{message});

	}

}
