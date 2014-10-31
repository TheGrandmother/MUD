package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public class HeartBeatMessage extends Message {

	//TODO this message are only sent from clients to notify the server they are still alive

	public HeartBeatMessage(String sender, long time_stamp) {
		super("Server", sender, "HeartBeat", time_stamp, null);
	}

	public HeartBeatMessage(String sender) {
		super("Server", sender, "HeartBeat", System.currentTimeMillis(), null);
	}
}