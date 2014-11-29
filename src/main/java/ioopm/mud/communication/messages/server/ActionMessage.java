package ioopm.mud.communication.messages.server;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

/**
 * 
 * These are messages corresponding to a requests by a player to perform an action
 * 
 * @author TheGrandmother
 *
 */
public class ActionMessage extends Message {
	/**
	 * 
	 * @param receiver who is to receive the message
	 * @param sender Who sent the message
	 * @param action What type of action
	 * @param nouns The arguments to the actions	
	 */
	public ActionMessage(String receiver, String sender, String action, String[] nouns) {
		super(receiver, sender, MessageType.GENERAL_ACTION, action, nouns);
	}
}
