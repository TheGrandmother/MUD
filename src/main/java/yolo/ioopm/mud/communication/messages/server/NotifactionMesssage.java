package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
/**
 * 
 * Messages for notifications i.e broadcasts.
 * 
 * @author TheGrandmother
 *
 */
public class NotifactionMesssage extends Message {

	public NotifactionMesssage(String receiver, String message) {
		super(receiver, "server", MessageType.NOTIFICATION, null, message);
		// TODO Auto-generated constructor stub
	}

}
