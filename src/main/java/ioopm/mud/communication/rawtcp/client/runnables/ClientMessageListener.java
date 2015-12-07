package ioopm.mud.communication.client.runnables;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMessageListener implements Runnable {

	private volatile boolean isRunning = true;

	private static final Logger logger = Logger.getLogger(ClientMessageListener.class.getName());

	private static final EnumSet<MessageType> ignored_types = EnumSet.of(
		MessageType.HEARTBEAT_REPLY
	);

	private final BufferedReader br;
	private final Queue<Message> inbox;

	/**
	 * Listens for messages on the given BufferedReader and adds them to the given inbox.
	 * @param br Reader to listen on
	 * @param inbox Inbox to put the messages in
	 */
	public ClientMessageListener(BufferedReader br, Queue<Message> inbox) {
		this.br = br;
		this.inbox = inbox;
	}

	@Override
	public void run() {

		logger.fine("Initiated!");

		// There is no need for this thread to sleep, br.readLine() is a blocking method.

		while(isRunning) {
			String data;
			synchronized(br) {
				try {
					logger.fine("Waiting for data...");
					data = br.readLine();
				}
				catch(IOException e) {
					logger.log(Level.SEVERE, "IOException when reading from BufferedReader! Terminating thread!", e);
					return;
				}
			}

			if(data == null) {
				logger.severe("Data was null after reading BufferedReader! Did the connection close? Terminating thread!");
				return;
			}

			Message msg = Message.deconstructTransmission(data);

			if(msg != null) {

				// Skip unwanted messages
				if(ignored_types.contains(msg.getType())) {
					continue;
				}

				logger.fine("Received msg: \"" + msg.getMessage() + "\"");
				logger.fine("Added message to inbox");
				inbox.offer(msg);
			}
			else {
				logger.warning("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
				continue;
			}
		}
	}

	/**
	 * Attempts to stop the infinite loop driving this runnable.
	 */
	public void stop() {
		isRunning = false;
	}
}
