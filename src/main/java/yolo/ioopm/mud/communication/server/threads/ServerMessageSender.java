package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.Mailbox;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerMessageSender extends Thread {

	private final ConcurrentHashMap<String, ClientConnection> connections;
	private final Mailbox<Message>                            outbox;

	public ServerMessageSender(ConcurrentHashMap<String, ClientConnection> connections, Mailbox<Message> outbox) {
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

			ArrayList<Message> messages = outbox.popAll();
			if(!messages.isEmpty()) {
				for(Message msg : messages) {
					ClientConnection cc = connections.get(msg.getReceiver());
					cc.write(msg.getMessage());
				}
			}
		}
	}
}
