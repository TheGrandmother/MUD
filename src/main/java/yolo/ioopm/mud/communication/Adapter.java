package yolo.ioopm.mud.communication;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Adapter {

	protected final ConcurrentLinkedQueue<Message> inbox  = new ConcurrentLinkedQueue<>();
	protected final ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();

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

	/**
	 * Pops the latest messages from the inbox-queue and returns them in an ArrayList.
	 *
	 * @return Returns latest messages.
	 * @throws CommunicationError
	 */
	public ArrayList<Message> popAllFromInbox() throws CommunicationError {
		ArrayList<Message> latest = new ArrayList<>();

		Message msg;
		while((msg = inbox.poll()) != null) {
			latest.add(msg);
		}

		return latest;
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
