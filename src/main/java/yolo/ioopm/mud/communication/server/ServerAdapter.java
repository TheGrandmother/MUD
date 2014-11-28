package yolo.ioopm.mud.communication.server;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.server.runnables.ServerConnectionListener;
import yolo.ioopm.mud.communication.server.runnables.ServerMessageListener;
import yolo.ioopm.mud.communication.server.runnables.ServerMessageSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ServerAdapter extends Adapter {

	private static final Logger logger = Logger.getLogger(ServerAdapter.class.getName());

	private final Map<String, ClientConnection> connections = new ConcurrentHashMap<>();
	private final Map<String, Long>             timestamps  = new ConcurrentHashMap<>();

	/**
	 * Creates a new adapter designed for the server side.
	 *
	 * @param port - Port to bind the server socket too.
	 * @throws IOException - if an I/O error occurs when opening the socket.
	 * @throws SecurityException - if a security manager exists and its checkListen method doesn't allow the operation.
	 * @throws IllegalArgumentException - if the port parameter is outside the specified range of valid port values, which is between 0 and 65535, inclusive.
	 */
	public ServerAdapter(int port) throws IOException, SecurityException, IllegalArgumentException {

		logger.fine("Initiating ServerAdapter!");

		// Async thread - Listens for new connections and adds them to connections.
		Thread scl = new Thread(new ServerConnectionListener(new ServerSocket(port), connections, timestamps, inbox, outbox));
		scl.setName("ServerConnectionListener");
		scl.start();

		// Async thread - Listens for new messages from the clients.
		// Needs outbox to be able to reply to heartbeats.
		Thread sml = new Thread(new ServerMessageListener(connections, inbox, outbox, timestamps));
		sml.setName("ServerMessageListener");
		sml.start();

		// Async thread - Sends the messages that are currently in the outbox.
		Thread sms = new Thread(new ServerMessageSender(connections, outbox));
		sms.setName("ServerMessageSender");
		sms.start();

		logger.fine("All threads started!");
	}
}
