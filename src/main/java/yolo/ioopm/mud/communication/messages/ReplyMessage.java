package yolo.ioopm.mud.communication.messages;

public class ReplyMessage extends OutgoingMessage {

	
	public ReplyMessage(String reciever, String[] nouns) {
		super(reciever, "server", "echo_reply", System.currentTimeMillis(), nouns);
		// TODO Auto-generated constructor stub
	}

}
