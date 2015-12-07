package ioopm.mud.communication.messages.server;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

/**
 * These messages are used to keep verify that a client is still connected
 * 
 * @author TheGrandmother
 */
public class HeartbeatReplyMessage extends Message {

	/**
	 * Constructs the message.
	 * @param receiver Name of the receiver.
	 */
	public HeartbeatReplyMessage(String receiver) {
		super(receiver, "server", MessageType.HEARTBEAT_REPLY, null);
	}
}
