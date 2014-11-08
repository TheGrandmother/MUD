package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ServerAdapter;

import java.io.IOException;

public class Server {

	protected Adapter adapter = null;

	public Server(int port, Adapter adapter) {

			this.adapter = adapter;

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
