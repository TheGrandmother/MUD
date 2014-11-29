package ioopm.mud.communication.messages.client;

import ioopm.mud.communication.MessageType;
import ioopm.mud.communication.Message;

/**
 * Sent from the client to the server when a player wants to perform an action.
 */
public class GeneralActionMessage extends Message {
	public GeneralActionMessage(String sender, String action, String[] nouns) {
		super("server", sender, MessageType.GENERAL_ACTION, action, nouns);
	}
}
