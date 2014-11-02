package yolo.ioopm.mud.communication.client.threads;

import yolo.ioopm.mud.communication.Mailbox;
import yolo.ioopm.mud.communication.Message;

import java.io.PrintWriter;

public class ClientMessageSender extends Thread {

	private final PrintWriter      pw;
	private final Mailbox<Message> outbox;

	public ClientMessageSender(PrintWriter pw, Mailbox<Message> outbox) {
		this.pw = pw;
		this.outbox = outbox;
	}

	@Override
	public void run() {
		while(true) {

			// Wait for a thread to notify that there are new messages.
			try {
				outbox.wait();
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			System.out.println("ClientMessageSender has been notified! Attempting to send messages!");

			// Send the messages.
			synchronized(pw) {
				for(Message msg : outbox.pollAll()) {
					pw.write(msg.getMessage());
				}
			}
		}
	}
}
