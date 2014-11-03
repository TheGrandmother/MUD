package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.messages.OutgoingMessage;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.util.Map;
import java.util.Queue;

public class ServerMessageSender implements Runnable {

	private final Map<String, ClientConnection> connections;
	private final Queue<OutgoingMessage>        outbox;

	public ServerMessageSender(Map<String, ClientConnection> connections, Queue<OutgoingMessage> outbox) {
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

			OutgoingMessage msg;
			while((msg = outbox.poll()) != null) {
				ClientConnection cc = connections.get(msg.getReceiver());
				cc.write(msg.getMessage());
			}
		}
	}
}
