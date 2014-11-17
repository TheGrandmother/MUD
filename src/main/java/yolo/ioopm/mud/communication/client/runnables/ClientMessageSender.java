package yolo.ioopm.mud.communication.client.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;

import java.io.PrintWriter;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMessageSender implements Runnable {

	private static final Logger logger = Logger.getLogger(ClientMessageSender.class.getName());

	private final PrintWriter    pw;
	private final Queue<Message> outbox;

	public ClientMessageSender(PrintWriter pw, Queue<Message> outbox) {
		this.pw = pw;
		this.outbox = outbox;
	}

	@Override
	public void run() {
		while(true) {

			// Iterate over outbox and send the messages every tick.
			try {
				logger.fine("Sleeping...");
				Thread.sleep(Adapter.TICKRATEMILLIS);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			logger.fine("Attempting to send any new messages");

			// Send the messages.
			synchronized(pw) {
				Message msg;
				while((msg = outbox.poll()) != null) {
					logger.fine("Sending msg: \"" + msg.getMessage() + "\"");
					pw.println(msg.getMessage());
				}
			}
		}
	}
}
