package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.util.Map;
import java.util.Queue;

public class ServerMessageSender implements Runnable {

	private final Map<String, ClientConnection> connections;
	private final Queue<Message>                outbox;

	public ServerMessageSender(Map<String, ClientConnection> connections, Queue<Message> outbox) {
		this.connections = connections;
		this.outbox = outbox;
	}

	@Override
	public void run() {
		//TODO sleep the thread after every iteration over the box
		while(true) {

			// Wait for new messages
			try {
				outbox.wait();
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			System.out.println("ServerMessageSender has been notified! Attempting to send messages!");

			Message msg;
			while((msg = outbox.poll()) != null) {
				ClientConnection cc = connections.get(msg.getReceiver());
				cc.write(msg.getMessage());
			}
		}
	}
}
