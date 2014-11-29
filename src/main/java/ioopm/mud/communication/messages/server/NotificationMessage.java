package ioopm.mud.communication.messages.server;

import ioopm.mud.communication.MessageType;
import ioopm.mud.communication.Message;

/**
 * 
 * Messages for notifications i.e broadcasts.
 * 
 * @author TheGrandmother
 *
 */
public class NotificationMessage extends Message {

	/**
	 * 
	 * Creates a notification message with the sender set to server and the message type set to{@literal MessageType#NOTIFICATION}.
	 * 
	 * @param receiver who the message is no be sent to
	 * @param message the content of the message
	 */
	public NotificationMessage(String receiver, String message) {
		super(receiver, "server", MessageType.NOTIFICATION, null, message);
		// TODO Auto-generated constructor stub
	}

}
