package ioopm.mud.communication.rawtcp.server.runnables;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;
import ioopm.mud.communication.messages.server.HandshakeReplyMessage;
import ioopm.mud.communication.rawtcp.server.ClientConnection;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectionVerifier implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerConnectionVerifier.class.getName());
	private final ClientConnection client;
	private final Map<String, ClientConnection> connections;
	private final Map<String, Long> timestamps;
	private volatile boolean isRunning = true;

	/**
	 * Listens for data from the client and tries to interpret it.
	 * If the clients communicates according to the protocol it adds the client to the connections and timestamps maps
	 * and any messages the client sent to the inbox.
	 *
	 * @param client      - Client to listen on.
	 * @param connections - Map to put the client in.
	 * @param timestamps  - as connections.
	 */
	public ServerConnectionVerifier(
		ClientConnection client,
		Map<String, ClientConnection> connections,
		Map<String, Long> timestamps
	) {
		this.client = client;
		this.connections = connections;
		this.timestamps = timestamps;
		logger.fine("New ServerConnectionVerifier created!");
	}

	@Override
	public void run() {
		while(isRunning) {
			String data;
			try {
				data = client.readLine();
			} catch(IOException e) {
				logger.log(Level.SEVERE, "IOException while listening for HandshakeMessage!", e);
				return;
			}

			if(data == null) {
				logger.severe("Received null data when listening for HandshakeMessage!");
				return;
			}

			Message msg = null;
			try {
				msg = Message.deconstructTransmission(data);
			} catch(IllegalArgumentException e) {
				logger.severe("Failed to deconstruct transmission! Transmission: \"" + data + "\"");
				return;
			}

			// Skip heartbeats
			if(msg.getType() == MessageType.HEARTBEAT) {
				logger.fine("Received heartbeat");
				continue;
			}

			if(msg.getType() == MessageType.HANDSHAKE) {
				if(!connections.containsKey(msg.getSender())) {
					logger.fine("New client sent a \"" + msg.getType() + "\" message, adding them to connections and message to inbox.");
					logger.info("New connection registered! IP: " + client.getIPAddress());

					connections.put(msg.getSender(), client);
					timestamps.put(msg.getSender(), System.currentTimeMillis());

					client.write(new HandshakeReplyMessage(true, "Welcome to the server!").getMessage());
				} else {
					logger.warning("New client tried to connect with name that is already connected!");
					client.write(new HandshakeReplyMessage(false, "That name is already connected").getMessage());
				}
			} else {
				logger.warning("New connection sent another type of message than a handshake message!");
			}

			return; // terminate thread
		}
	}

	/**
	 * Attempts to stop the infinite loop driving this runnable.
	 */
	public void stop() {
		isRunning = false;
	}
}
