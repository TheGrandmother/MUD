package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ServerMessageListener implements Runnable {

	private final Map<String, ClientConnection> connections;
	private final Queue<Message>                inbox;

	// This log keeps track of the latest timestamps
	private final Map<String, Long> timestamps;

	public ServerMessageListener(Map<String, ClientConnection> connections, Queue<Message> inbox, Map<String, Long> timestamps) {
		this.connections = connections;
		this.inbox = inbox;
		this.timestamps = timestamps;
	}

	@Override
	public void run() {
		while(true) {

			try {
				Thread.sleep(Adapter.TICKRATEMILLIS);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

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

						if(msg.getAction().equals("heartbeat")) {
							// Do nothing for now
						}
						else {
							inbox.offer(msg);
						}

						timestamps.put(entry.getKey(), msg.getTimeStamp());
					}
					else {
						System.err.println("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
					}
				}
				else {
					//The client has not sent any message, check if they are still alive
					long latest_time_stamp = timestamps.get(
							entry.getKey());

					long delta = System.currentTimeMillis() - latest_time_stamp;

					// If the client hasn't sent any messages during this time, mark them as dead.
					if(delta > Adapter.TIMEOUT_SECONDS) {
						dead_clients.add(entry.getKey());
					}
				}
			}

			// Remove any dead clients
			if(dead_clients.size() != 0) {
				System.out.format("ServerMessageListener will be removing %d dead clients!%n", dead_clients.size());

				for(String client : dead_clients) {
					connections.remove(client);
					System.out.println(client + " timed out!");
				}
			}
		}
	}
}
