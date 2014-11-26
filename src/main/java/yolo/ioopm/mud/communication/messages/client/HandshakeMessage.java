package yolo.ioopm.mud.communication.messages.client;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

public class HandshakeMessage extends Message {
	/**
	 * Creates a new message.
	 *
	 * @param sender
	 */
	public HandshakeMessage(String sender) {
		super("server", sender, MessageType.HANDSHAKE, null, null);
	}
}
