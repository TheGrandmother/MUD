package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectionVerifier implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerConnectionVerifier.class.getName());

	private final ClientConnection              client;
	private final Map<String, ClientConnection> connections;
	private final Map<String, Long>             timestamps;
	private final Queue<Message>                inbox;
	private final Queue<Message>                outbox;

	/**
	 * Listens for data from the client and tries to interpret it.
	 * If the clients communicates according to the protocol it adds the client to the connections and timestamps maps
	 * and any messages the client sent to the inbox.
	 *
	 * @param client - Client to listen on.
	 * @param connections - Map to put the client in.
	 * @param timestamps - as connections.
	 * @param inbox - Where to put ingoing messages.
	 * @param outbox - Where to put outgoing messages.
	 */
	public ServerConnectionVerifier(
			ClientConnection client,
			Map<String, ClientConnection> connections,
			Map<String, Long> timestamps,
			Queue<Message> inbox,
			Queue<Message> outbox
	) {
		this.client = client;
		this.connections = connections;
		this.timestamps = timestamps;
		this.inbox = inbox;
		this.outbox = outbox;
		logger.fine("New ServerConnectionVerifier created!");
	}

	@Override
	public void run() {
		while(true) {
			String data;
			try {
				data = client.readLine();
			}
			catch(IOException e) {
				logger.log(Level.SEVERE, "IOException while listening for ClientRegistrationMessage!", e);
				return;
			}

			if(data == null) {
				logger.severe("Received null data when listening for ClientRegistrationMessage!");
				return;
			}

			Message msg = Message.deconstructTransmission(data);

			if(msg == null) {
				logger.severe("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
				return;
			}

			// Skip heartbeats
			if(msg.getType() == MessageType.HEARTBEAT) {
				logger.fine("Received heartbeat");
				continue;
			}

			if(!connections.containsKey(msg.getSender())) {
				logger.fine("New client sent a \"" + msg.getType() + "\" message, adding them to connections and message to inbox.");
				logger.info("New connection registered! IP: " + client.getIPAdress());

				inbox.offer(msg);
				connections.put(msg.getSender(), client);
				timestamps.put(msg.getSender(), System.currentTimeMillis());
			}
			else {
				logger.warning("New client tried to connect with name that is already connected!");
			}

			return; // terminate thread
		}
	}
}
