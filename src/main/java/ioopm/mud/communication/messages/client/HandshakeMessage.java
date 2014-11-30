package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

/**
 * Sent from client to server to start a new connection.
 */
public class HandshakeMessage extends Message {
	/**
	 * Creates a new message.
	 *
	 * @param sender Name of the sender.
	 */
	public HandshakeMessage(String sender) {
		super("server", sender, MessageType.HANDSHAKE, null);
	}
}
