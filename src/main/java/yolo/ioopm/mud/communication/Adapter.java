package yolo.ioopm.mud.communication;

import yolo.ioopm.mud.communication.messages.IncommingMessage;
import yolo.ioopm.mud.communication.messages.OutgoingMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Adapter {

	protected final ConcurrentLinkedQueue<IncommingMessage> inbox  = new ConcurrentLinkedQueue<>();
	protected final ConcurrentLinkedQueue<OutgoingMessage>  outbox = new ConcurrentLinkedQueue<>();

	/**
	 * Polls the oldest message from the inbox.
	 *
	 * @return Retrieves and removes head of inbox, null if inbox is empty.
	 */
	public IncommingMessage poll() {
		return inbox.poll();
	}

	/**
	 * Tries to send a message trough the adapter
	 *
	 * @param message
	 */
	public void sendMessage(final OutgoingMessage message) {

		// The adding is made in a new thread so the main thread isn't blocked if the outbox is currently locked.
		new Thread(new Runnable() {
			@Override
			public void run() {
				outbox.offer(message);

				// Notify the sender that there are new messages waiting.
				outbox.notifyAll();
			}
		}).start();
	}
}
