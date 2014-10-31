package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.Mailbox;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMessageListener extends Thread {

	private final Map<String, ClientConnection> connections;
	private final Mailbox<Message>              inbox;

	// This log keeps track of the latest timestamps
	private final Map<String, Long> timestamps;

	public ServerMessageListener(Map<String, ClientConnection> connections, Mailbox<Message> inbox, Map<String, Long> timestamps) {
		this.connections = connections;
		this.inbox = inbox;
		this.timestamps = timestamps;
	}

	@Override
	public void run() {
		while(true) {

			Set<String> dead_clients = new HashSet<>();

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

						// Add the message to the inbox if it's not a heartbeat.
						if(!msg.getAction().equals("HeartBeat")) {
							inbox.add(msg);
						}

						timestamps.put(entry.getKey(), msg.getTimeStamp());
					}
					else {
						System.out.println("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
					}
				}
				else {
					//The client has not sent any message, check if they are still alive
					long latest_time_stamp = timestamps.get(entry.getKey());

					long delta = System.currentTimeMillis() - latest_time_stamp;

					if(delta > 30 * 1000) {
						dead_clients.add(entry.getKey());
					}
				}
			}

			// Remove any dead clients
			for(String client : dead_clients) {
				connections.remove(client);
				System.out.println(client + " timed out!");
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
