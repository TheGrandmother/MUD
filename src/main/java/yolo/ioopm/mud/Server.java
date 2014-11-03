package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.IncomingMessage;
import yolo.ioopm.mud.communication.messages.OutgoingMessage;
import yolo.ioopm.mud.communication.server.ServerAdapter;

import java.io.IOException;

public class Server {

	private Adapter adapter = null;

	public Server(int port) {
		try {
			adapter = new ServerAdapter(port);
		}
		catch(IOException e) {
			System.err.format("Server failed to create ServerAdapter on port: %d%n", port);
			e.printStackTrace();
		}
	}

	public IncomingMessage pollOldestMessage() {
		return adapter.poll();
	}

	public void sendMessage(OutgoingMessage msg) {
		adapter.sendMessage(msg);
	}
}
