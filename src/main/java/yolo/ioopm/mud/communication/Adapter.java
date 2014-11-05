package yolo.ioopm.mud.communication;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Adapter {

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
