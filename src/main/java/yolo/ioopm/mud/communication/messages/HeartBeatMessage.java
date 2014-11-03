package yolo.ioopm.mud.communication.messages;

import yolo.ioopm.mud.communication.Message;

public class HeartBeatMessage extends Message {

	//TODO this message are only sent from clients to notify the server they are still alive
	//This is a dummy message that is never added to the servers inbox, but it is logged in ServerMessageListener

	public HeartBeatMessage(String sender, long time_stamp) {
		super("server", sender, "HeartBeat", time_stamp, null);
	}

	public HeartBeatMessage(String sender) {
		super("server", sender, "HeartBeat", System.currentTimeMillis(), null);
	}
}