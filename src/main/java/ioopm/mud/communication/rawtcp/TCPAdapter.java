package ioopm.mud.communication.rawtcp;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class TCPAdapter implements Adapter {

	/*
		This is the amount of times per second senders and listeners will iterate over the queues and send/retrieve messages
		1 => 1 time/s
		2 => 2 times/s
		.. etc
	 */
	public final static int TICKRATE = 5;
	// This is the actual value the listeners/senders read.
	public final static int TICKRATEMILLIS = 1000 / TICKRATE;
	// Time in milliseconds between each heartbeat message.
	public final static int HEARTBEAT_FREQUENCY = 5000;
	// This defines the time the ServerMessageListener will wait before it marks a client as dead and removes it.
	public final static int TIMEOUT_SECONDS = HEARTBEAT_FREQUENCY * 5;
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
