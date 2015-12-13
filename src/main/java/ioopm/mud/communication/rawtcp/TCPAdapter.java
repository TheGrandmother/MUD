package ioopm.mud.communication.rawtcp;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class TCPAdapter implements Adapter {

	private static final Logger logger = Logger.getLogger(TCPAdapter.class.getName());
	protected final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<>();
	protected final ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();

	public Message poll() {
		Message msg = inbox.poll();

		if(msg != null) {
			logger.fine("Popping msg \"" + msg.getMessage() + "\".");
		}

		return msg;
	}

	public void sendMessage(final Message message) {

		logger.fine("Adding message \"" + message.getMessage() + "\" to inbox.");

		// The adding is made in a new thread so the main thread isn't blocked if the outbox is currently locked.
		new Thread(new Runnable() {
			@Override
			public void run() {
				outbox.offer(message);
			}
		}).start();
	}
}
