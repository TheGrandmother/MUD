package ioopm.mud.communication.rawtcp.client.runnables;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;
import ioopm.mud.communication.rawtcp.TCPAdapter;

import java.io.PrintWriter;
import java.util.Queue;
import java.util.logging.Logger;

public class ClientMessageSender implements Runnable {

	private volatile boolean isRunning = true;

	private static final Logger logger = Logger.getLogger(ClientMessageSender.class.getName());

	private final PrintWriter    pw;
	private final Queue<Message> outbox;

	/**
	 * Sends the messages in the given outbox to the given PrintWriter.
	 * @param pw PrintWriter to write to
	 * @param outbox Outbox to poll from
	 */
	public ClientMessageSender(PrintWriter pw, Queue<Message> outbox) {
		this.pw = pw;
		this.outbox = outbox;
	}

	@Override
	public void run() {
		while(isRunning) {

			// Iterate over outbox and send the messages every tick.
			try {
				Thread.sleep(TCPAdapter.TICKRATEMILLIS);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			// Send the messages.
			synchronized(pw) {
				Message msg;
				while((msg = outbox.poll()) != null) {
					if(msg.getType() != MessageType.HEARTBEAT) {
						logger.fine("Sending msg: \"" + msg.getMessage() + "\"");
					}
					pw.println(msg.getMessage());
				}

				// Allows for testing, not used during actual runtime.
				pw.notify();
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
