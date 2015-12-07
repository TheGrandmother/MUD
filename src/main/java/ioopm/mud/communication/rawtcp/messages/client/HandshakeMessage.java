package ioopm.mud.communication.rawtcp.messages.client;

import ioopm.mud.communication.rawtcp.Message;
import ioopm.mud.communication.rawtcp.MessageType;

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
