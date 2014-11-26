package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * 
 * A message returned after a connection has requested to register a player in the server.
 * 
 * @author TheGrandmother
 *
 */
public class RegistrationReplyMessage extends Message{
	/**
	 * 
	 * @param receiver the name of the connection who wanted to register
	 * @param success Weather or not the registration was successful
	 */
	public RegistrationReplyMessage(String receiver, boolean success) {
		super(receiver, "server", MessageType.REGISTRATION_REPLY, null, new String[]{String.valueOf(success)});
	}
}
