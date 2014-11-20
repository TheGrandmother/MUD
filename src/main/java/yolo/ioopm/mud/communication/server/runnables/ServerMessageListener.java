package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.messages.server.HeartbeatReplyMessage;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMessageListener implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerMessageListener.class.getName());

	/*
	 	These message types will not be added to the inbox when received!
	 	Their timestamp however will be logged.
	  */
	private static final EnumSet<MessageType> ignored_messages = EnumSet.of(
		MessageType.HEARTBEAT
	);

	private final Map<String, ClientConnection> connections;
	private final Queue<Message>                inbox;
	private final Queue<Message>                outbox;

	// This log keeps track of the latest timestamps
	private final Map<String, Long> timestamps;

	public ServerMessageListener(Map<String, ClientConnection> connections, Queue<Message> inbox, Queue<Message> outbox, Map<String, Long> timestamps) {
		this.connections = connections;
		this.inbox = inbox;
		this.outbox = outbox;
		this.timestamps = timestamps;
	}

	@Override
	public void run() {
		while(true) {

			try {
				Thread.sleep(Adapter.TICKRATEMILLIS);
			}
			catch(InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}

			Set<String> dead_clients = new HashSet<>();

			for(Map.Entry<String, ClientConnection> entry : connections.entrySet()) {
				ClientConnection cc = entry.getValue();

				String data = null;

				try {
					if(cc.hasUnreadData()) {
						logger.fine("Reading data from client...");
						data = cc.readLine();
					}
				}
				catch(IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}

				if(data != null) {
					Message msg = Message.deconstructTransmission(data);

					if(msg != null) {
						if(!ignored_messages.contains(msg.getType())) {
							logger.fine("Received message of type \"" + msg.getType() + "\" from user \"" + msg.getSender() + "\"");
							inbox.offer(msg);
						}
						else if(msg.getType() == MessageType.HEARTBEAT) {
//							logger.finest("Responding to client heartbeat!");
							outbox.offer(new HeartbeatReplyMessage(msg.getSender()));
						}

//						logger.fine("Adding timestamp to log");
						timestamps.put(entry.getKey(), msg.getTimeStamp());
					}
					else {
						logger.severe("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
					}
				}
				else {
					//The client has not sent any message, check if they are still alive
					long latest_time_stamp = timestamps.get(entry.getKey());

					long delta = System.currentTimeMillis() - latest_time_stamp;

					// If the client hasn't sent any messages during this time, mark them as dead.
					if(delta > Adapter.TIMEOUT_SECONDS) {
						logger.fine("Found dead client!");
						dead_clients.add(entry.getKey());
					}
				}
			}

			// Remove any dead clients
			if(dead_clients.size() != 0) {
				logger.fine("ServerMessageListener will be removing " + dead_clients.size() + " dead clients!");

				for(String client : dead_clients) {
					connections.remove(client);
					logger.info(client + " timed out!");
				}
			}
		}
	}
}
