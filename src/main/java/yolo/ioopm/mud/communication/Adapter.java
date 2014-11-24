package yolo.ioopm.mud.communication;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public abstract class Adapter {

	private static final Logger logger = Logger.getLogger(Adapter.class.getName());

	/*
		This is the amount of times per second senders and listeners will iterate over the queues and send/retrieve messages
		1 => 1 time/s
		2 => 2 times/s
		.. etc
	 */
	public final static int TICKRATE = 2;

	// This is the actual value the listeners/senders read.
	public final static int TICKRATEMILLIS = 1000 / TICKRATE;

	// Time in milliseconds between each heartbeat message.
	public final static int HEARTBEAT_FREQUENCY = 5000;

	// This defines the time the ServerMessageListener will wait before it marks a client as dead and removes it.
	public final static int TIMEOUT_SECONDS = HEARTBEAT_FREQUENCY * 5;

	protected final ConcurrentLinkedQueue<Message> inbox  = new ConcurrentLinkedQueue<>();
	protected final ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();

	/**
	 * Polls the oldest message from the inbox.
	 *
	 * @return Retrieves and removes head of inbox, null if inbox is empty.
	 */
	public Message poll() {
		Message msg = inbox.poll();

		if(msg != null) {
			logger.fine("Popping msg \"" + msg.getMessage() + "\".");
		}

		return msg;
	}

	//TODO
	//Fix so that we can adda message to say stuff like, username does not exist and things like that.
	/**
	 * Tries to send a message trough the adapter
	 *
	 * @param message
	 */
	public void sendMessage(final Message message) {

		logger.fine("Adding message \"" + message.getMessage() + "\" to inbox.");

		// The adding is made in a new thread so the main thread isn't blocked if the outbox is currently locked.
		new Thread(() -> outbox.offer(message)).start();
	}
}
