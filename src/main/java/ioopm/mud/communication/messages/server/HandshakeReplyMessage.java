package ioopm.mud.communication.messages.server;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;

public class HandshakeReplyMessage extends Message {

	/**
	 * Constructs the message.
	 *
	 * @param success True if the connection is allowed.
	 * @param reason  If the connection is not allowed, this is the reason why.
	 */
	public HandshakeReplyMessage(boolean success, String reason) {
		super("unknown", "server", MessageType.HANDSHAKE_REPLY, null, String.valueOf(success), reason);
	}
}
