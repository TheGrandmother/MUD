package yolo.ioopm.mud.communication.messages.protocol;

import yolo.ioopm.mud.communication.messages.IncomingMessage;

public class HeartBeatMessage extends IncomingMessage {
	public HeartBeatMessage(String sender) {
		super("server", sender, "heartbeat", System.currentTimeMillis(), null);
	}
}
