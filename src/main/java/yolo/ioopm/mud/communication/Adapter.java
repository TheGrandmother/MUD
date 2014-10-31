package yolo.ioopm.mud.communication;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Adapter {

	protected final Mailbox<Message> inbox  = new Mailbox<>();
	protected final Mailbox<Message> outbox = new Mailbox<>();

	public ArrayList<Message> popAllIncoming() {
		return inbox.popAll();
	}

	/**
	 * Tries to send a message trough the adapter
	 *
	 * @param message
	 * @throws CommunicationError
	 */
	public void sendMessage(final Message message) throws CommunicationError {

		// The adding is made in a new thread so the main thread isn't blocked if the outbox is currently locked.
		new Thread(new Runnable() {
			@Override
			public void run() {
				outbox.add(message);
			}
		}).start();
	}

	@SuppressWarnings("serial")
	public class CommunicationError extends Exception {

		public CommunicationError() {
			super();
		}

		public CommunicationError(String message) {
			super(message);
		}
	}
}
