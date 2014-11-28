package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
/**
 * 
 * This message is used for errors caused by the users actions. These are errors for which the player is responsible and should therefore be notified.
 * 
 * @author TheGrandmother
 */
public class ErrorMessage extends Message {
	
	/**
	 * Creates a new error message to be sent to the player.<p>
	 * The sender will always be the server and the message type field is set to {@literal MessageType#GENERAL_ERROR} and no action is sent.
	 * 
	 * @param receiver Who the message is to me sent to
	 * @param error_message The contents of the message
	 */
	public ErrorMessage(String receiver, String error_message) {
		super(receiver, "server", MessageType.GENERAL_ERROR, null, error_message);
	}
}
