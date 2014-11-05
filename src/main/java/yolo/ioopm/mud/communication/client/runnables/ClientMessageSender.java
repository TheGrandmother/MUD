package yolo.ioopm.mud.communication.client.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;

import java.io.PrintWriter;
import java.util.Queue;

public class ClientMessageSender implements Runnable {

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
				Thread.sleep(Adapter.TICKRATEMILLIS);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			System.out.println("ClientMessageSender has been notified! Attempting to send messages!");

			// Send the messages.
			synchronized(pw) {
				Message msg;
				while((msg = outbox.poll()) != null) {
					pw.println(msg.getMessage());
				}
			}
		}
	}
}
