package yolo.ioopm.mud.communication;

import java.util.ArrayList;

public abstract class Adapter {

	protected final Mailbox<Message> inbox  = new Mailbox<>();
	protected final Mailbox<Message> outbox = new Mailbox<>();

	/**
	 * This method calls wait() on the inbox.
	 * Essentially blocks the thread until a notify() has been called on the inbox.
	 *
	 * @throws InterruptedException
	 */
	public void waitForNewMessages() throws InterruptedException {
		inbox.wait();
	}

	/**
	 * Empties the inbox and returns the messages in an arraylist.
	 *
	 * @return - ArrayList with all messages in the inbox.
	 */
	public ArrayList<Message> pollInbox() {
		return inbox.pollAll();
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

				// Notify the sender that there are new messages waiting.
				outbox.notifyAll();
			}
		}).start();
	}
}
