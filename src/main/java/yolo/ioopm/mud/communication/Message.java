package yolo.ioopm.mud.communication;


/**
 * 	This is the class which specifies the messages. These messages are sent to the Adapter and from there
 * 	translated and sent to the server.
 * 
 * Se specs/message-protocol.txt for a description of the message protocol.
 * 
 * @author TheGrandmother
 *
 */

public class Message {

	String message;
	String reciever;
	String sender;
	String action;
	String[] nouns;
	String time_stamp;
	
	
	/**
	 * This will be a constructor which creates a message to be sent to the Adapter
	 * 
	 * @param reciever
	 * @param sender
	 * @param action
	 * @param nouns
	 */
	public Message(String reciever, String sender, String action, String[] nouns) {

	}
	
	
	/**
	 * 
	 * This function takes a string received by the adapter and converts it to a Message object;
	 * 
	 * @param transmission
	 * @return
	 */
	public static Message deconstructTransmission(String transmission){
		return null;
		
	}
}
