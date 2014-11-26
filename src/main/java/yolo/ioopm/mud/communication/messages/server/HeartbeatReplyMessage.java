package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * Sent from server to client as a respond to client heart beats.
 */
public class HeartbeatReplyMessage extends Message {
	public HeartbeatReplyMessage(String receiver) {
		super(receiver, "server", MessageType.HEARTBEAT_REPLY, null, null);
	}
}
