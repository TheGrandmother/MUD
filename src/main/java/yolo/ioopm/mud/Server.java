package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ServerAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class Server {

	private Adapter adapter = null;

	public Server(int port) {
		try {
			adapter = new ServerAdapter(port);
		}
		catch(IOException e) {
			System.err.format("Server failed to create ServerAdapter on port: %d%n", port);
			e.printStackTrace();
			return;
		}

		//This will block this thread until the ServerMessageListener has received new messages
		try {
			adapter.waitForNewMessages();
		}
		catch(InterruptedException e) {
			//TODO unhandled exception
			e.printStackTrace();
		}

		// Retrieve all new messages
		ArrayList<Message> new_messages = adapter.pollInbox();
	}
}
