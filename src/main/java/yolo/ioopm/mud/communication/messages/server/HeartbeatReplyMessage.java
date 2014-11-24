package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class HeartbeatReplyMessage extends Message {
	public HeartbeatReplyMessage(String receiver) {
		super(receiver, "server", MessageType.HEARTBEAT_REPLY, null, null);
	}
}
