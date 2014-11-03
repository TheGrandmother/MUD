package yolo.ioopm.mud.communication.messages;

public class ClientRegistrationMessage extends IncommingMessage {

	// This message is sent when an unknown client wants to be registered
	//TODO define protocol

	public ClientRegistrationMessage(String sender, long time_stamp) {
		super("server", sender, "ClientRegistration", time_stamp, null);
	}
}
