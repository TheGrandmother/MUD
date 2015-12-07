package ioopm.mud.communication.rawtcp.messages.server;

import ioopm.mud.communication.rawtcp.Message;
import ioopm.mud.communication.rawtcp.MessageType;

/**
 * 
 * These messages are reserved for serious errors. These should not be cast as a result of the players action.
 * These messages represents serious errors in the server and is an indication that something really bad has happened.
 * 
 * @author TheGrandmother
 *
 */
public class SeriousErrorMessage extends Message {

	
	/**
	 * Creates a new message of the type {@literal MessageType#SERIOUS_ERROR}.
	 * 
	 * @param receiver Who the message is to be sent to
	 * @param message the contents of the message
	 */
	public SeriousErrorMessage(String receiver, String message) {
		super(receiver, "server", MessageType.SERIOUS_ERROR, null, message);

	}

}
