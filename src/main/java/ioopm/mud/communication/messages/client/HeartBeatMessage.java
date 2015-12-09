package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;

/**
 * Sent from client to server on a regular basis to indicate that the connection is still alive.
 */
public class HeartBeatMessage extends Message {
	/**
	 * Constructs the message.
	 *
	 * @param sender Name of the sender.
	 */
	public HeartBeatMessage(String sender) {
		super("server", sender, MessageType.HEARTBEAT, null);
	}
}
