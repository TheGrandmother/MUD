package ioopm.mud.communication.messages.server;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

public class HandshakeReplyMessage extends Message {
	/**
	 * Creates a new message.
	 *
	 */
	public HandshakeReplyMessage(boolean success, String reason) {
		super("unknown", "server", MessageType.HANDSHAKE_REPLY, null, String.valueOf(success), reason);
	}
}
