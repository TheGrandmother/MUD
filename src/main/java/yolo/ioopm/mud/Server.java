package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ServerAdapter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	public final static int DEFAULT_PORT = 1337;

	private Adapter adapter = null;

	public Server() {
		logger.fine("Attempting to create server adapter...");
		try {
			adapter = new ServerAdapter(DEFAULT_PORT);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, "Server failed to create ServerAdapter on port: " + DEFAULT_PORT, e);
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
