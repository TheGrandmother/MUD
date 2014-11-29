package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

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
