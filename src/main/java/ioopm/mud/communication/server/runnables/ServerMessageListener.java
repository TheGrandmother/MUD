package ioopm.mud.communication.server.runnables;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.MessageType;
import ioopm.mud.communication.messages.client.LogoutMessage;
import ioopm.mud.communication.messages.server.HeartbeatReplyMessage;
import ioopm.mud.communication.Message;
import ioopm.mud.communication.server.ClientConnection;

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

	/**
	 * Listens for data from clients.
	 * If the data is correctly formatted as a message it will be added to the given inbox-queue.
	 *
	 * It will automatically respond to any heartbeats any client would send. It does this by
	 * adding a HeartBeatReplyMessage to the given outbox-queue.
	 *
	 * It will also keep track of the last time a client sent a message.
	 * If a client has not sent any messages (including heartbeats) within the
	 * time range defined in the adapter it will mark that client as dead and terminate their connection.
	 *
	 * @param connections - Map where client usernames are mapped to their respective ClientConnection object.
	 * @param inbox - Where to put any new messages the clients send.
	 * @param outbox - Where to put respond messages to heartbeats.
	 * @param timestamps - Where to keep track of the latest timestamp in messages from clients.
	 */
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
							logger.info("Received message: \"" + data + "\"");
							inbox.offer(msg);

							if(msg.getType() == MessageType.LOGOUT) {
								logger.fine(entry.getKey() + " sent logout, will now be marked as dead.");
								dead_clients.add(entry.getKey());
							}
						}
						else if(msg.getType() == MessageType.HEARTBEAT) {
							outbox.offer(new HeartbeatReplyMessage(msg.getSender()));
						}

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

						logger.fine("Adding new LogoutMessage for dead client");
						inbox.offer(new LogoutMessage(entry.getKey()));
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
