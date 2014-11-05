package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.ClientLoginMessage;

import java.io.IOException;

public class Client {

	//TODO read these values from user
	private final String USERNAME = "Player";
	private final String PASSWORD = "asdf1234";

	private Adapter adapter = null;

	public Client(String host, int port) {
		try {
			adapter = new ClientAdapter(host, port, USERNAME);
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		// Authenticate against server
		Message msg = new ClientLoginMessage(USERNAME, PASSWORD);
		adapter.sendMessage(msg);
	}
}
