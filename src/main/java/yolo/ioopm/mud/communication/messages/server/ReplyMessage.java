package yolo.ioopm.mud.communication.messages.server;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;

/**
 * 
 * These errors are replies to the players after an action has been successfully executed.
 * 
 * @author TheGrandmother
 *
 */
public class ReplyMessage extends Message {
	/**
	 * 
	 * Creates a new message of type {@literal MessageType#GENERAL_REPLY}
	 * 
	 * @param reciever who the message is to be sent to
	 * @param action the action of the message. 
	 * @param nouns the contents of the message
	 */
	public ReplyMessage(String reciever, String action, String... nouns) {
		super(reciever, "server", MessageType.GENERAL_REPLY, action, nouns);
	}
}
