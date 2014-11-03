package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.util.Map;

public class ServerConnectionVerifier implements Runnable {

	private final ClientConnection              client;
	private final Map<String, ClientConnection> connections;

	public ServerConnectionVerifier(ClientConnection client, Map<String, ClientConnection> connections) {
		this.client = client;
		this.connections = connections;
	}

	@Override
	public void run() {

		String data;
		try {
			data = client.readLine();
		}
		catch(IOException e) {
			System.out.println("IOException while listening for ClientRegistrationMessage!");
			e.printStackTrace();
			return;
		}

		if(data == null) {
			System.out.println("Received null data when listening for ClientRegistrationMessage!");
			return;
		}

		//TODO parse data to message, only accept ClientRegistrationMessages, then add client to connections
	}
}
