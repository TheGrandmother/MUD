package ioopm.mud.communication;

import ioopm.mud.communication.messages.Message;

public interface Adapter {

	/*
		This is the amount of times per second senders and listeners will iterate over the queues and send/retrieve messages
		1 => 1 time/s
		2 => 2 times/s
		.. etc
	 */
	int TICKRATE = 5;

	// This is the actual value the listeners/senders read.
	int TICKRATEMILLIS = 1000 / TICKRATE;

	// Time in milliseconds between each heartbeat message.
	int HEARTBEAT_FREQUENCY = 5000;

	// This defines the time the ServerMessageListener will wait before it marks a client as dead and removes it.
	int TIMEOUT_SECONDS = HEARTBEAT_FREQUENCY * 5;

	/**
	 * Polls the oldest message from the inbox.
	 *
	 * @return - Retrieves and removes head of inbox, null if inbox is empty.
	 */
	Message poll();

	/**
	 * Tries to send a message trough the adapter
	 *
	 * @param m - The message to send.
	 */
	void sendMessage(Message m);
}
