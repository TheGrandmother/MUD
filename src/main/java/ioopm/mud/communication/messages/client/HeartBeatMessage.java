package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

public class HeartBeatMessage extends Message {
	public HeartBeatMessage(String sender) {
		super("server", sender, MessageType.HEARTBEAT, null, null);
	}
}
