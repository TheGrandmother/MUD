package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
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

	protected Server(Adapter a) {
		adapter = a;
	}
	
	protected Adapter getAdapter(){
		return adapter;
	}

	/**
	 * Retrieves and removes oldest message from the inbox-queue.
	 *
	 * @return Oldest message in inbox, null if inbox is empty.
	 */
	public Message pollOldestMessage() {
		return adapter.poll();
	}

	/**
	 * Adds the message to the outbox for the adapter to send in the future.
	 *
	 * @param msg
	 */
	public void sendMessage(Message msg) {
		adapter.sendMessage(msg);
	}
}
