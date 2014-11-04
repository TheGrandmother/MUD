package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;

public class HeartBeatMessage extends Message {
	public HeartBeatMessage(String sender) {
		super("server", sender, "heartbeat", System.currentTimeMillis(), null);
	}
}
