package yolo.ioopm.mud.communication.client;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;

public class ClientAdapter implements Adapter {
	@Override
	public void sendMessage(Message message) throws CommunicationError {

	}

	@Override
	public Message pollForMessage() throws CommunicationError {
		return null;
	}
}
