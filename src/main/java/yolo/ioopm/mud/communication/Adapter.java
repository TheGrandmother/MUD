package yolo.ioopm.mud.communication;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Adapter {

	protected final ConcurrentLinkedQueue<Message> inbox  = new ConcurrentLinkedQueue<>();
	protected final ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();

	/**
	 * Polls the oldest message from the inbox.
	 *
	 * @return Retrieves and removes head of inbox, null if inbox is empty.
	 */
	public Message poll() {
		return inbox.poll();
	}

	/**
	 * Tries to send a message trough the adapter
	 *
	 * @param message
	 */
	public void sendMessage(final Message message) {

		// The adding is made in a new thread so the main thread isn't blocked if the outbox is currently locked.
		new Thread(new Runnable() {
			@Override
			public void run() {
				outbox.offer(message);
			}
		}).start();
	}
}
