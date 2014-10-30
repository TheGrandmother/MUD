package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.Mailbox;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerMessageListener extends Thread {

	private final ConcurrentHashMap<String, ClientConnection> connections;
	private final Mailbox<Message>                            inbox;

	public ServerMessageListener(ConcurrentHashMap<String, ClientConnection> connections, Mailbox<Message> inbox) {
		this.connections = connections;
		this.inbox = inbox;
	}

	@Override
	public void run() {
		while(true) {

			for(Map.Entry<String, ClientConnection> entry : connections.entrySet()) {
				ClientConnection cc = entry.getValue();

				String data = null;

				try {
					if(cc.hasUnreadData()) {
						data = cc.readLine();
					}
				}
				catch(IOException e) {
					//TODO unhandled exception
					e.printStackTrace();
				}

				if(data != null) {
					Message msg = Message.deconstructTransmission(data);

					if(msg != null) {
						inbox.add(msg);
					}
					else {
						System.out.println("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
					}
				}
			}

			// Notify all waiting threads that there are new messages.
			if(inbox.size() > 0) {
				inbox.notifyAll();
			}

			try {
				Thread.sleep(500);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}
		}
	}
}
