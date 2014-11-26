package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * 
 * These messages are used to keep verify that a client is still connected
 * 
 * @author TheGrandmother
 *
 */
public class HeartbeatReplyMessage extends Message {
	/**
	 * 
	 * @param receiver who sent the heartbeat
	 */
	public HeartbeatReplyMessage(String receiver) {
		super(receiver, "server", MessageType.HEARTBEAT_REPLY, null, null);
	}
}
