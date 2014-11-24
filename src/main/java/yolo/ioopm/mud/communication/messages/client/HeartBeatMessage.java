package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class HeartBeatMessage extends Message {
	public HeartBeatMessage(String sender) {
		super("server", sender, MessageType.HEARTBEAT, null, System.currentTimeMillis(), null);
	}
}
