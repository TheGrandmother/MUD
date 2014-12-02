package ioopm.mud.communication.client.runnables;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;

import java.io.PrintWriter;
import java.util.Queue;
import java.util.logging.Logger;

public class ClientMessageSender implements Runnable {

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
		while(true) {

			// Iterate over outbox and send the messages every tick.
			try {
				Thread.sleep(Adapter.TICKRATEMILLIS);
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

				pw.notifyAll();
			}
		}
	}
}
