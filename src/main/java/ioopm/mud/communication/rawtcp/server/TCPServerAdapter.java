package ioopm.mud.communication.rawtcp.server;

import ioopm.mud.communication.rawtcp.TCPAdapter;
import ioopm.mud.communication.rawtcp.server.runnables.ServerMessageListener;
import ioopm.mud.communication.rawtcp.server.runnables.ServerMessageSender;
import ioopm.mud.communication.rawtcp.server.runnables.ServerConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class TCPServerAdapter extends TCPAdapter{

	private static final Logger logger = Logger.getLogger(TCPServerAdapter.class.getName());

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
	public TCPServerAdapter(int port) throws IOException, SecurityException, IllegalArgumentException {

		logger.fine("Initiating TCPServerAdapter!");

		// Async thread - Listens for new connections and adds them to connections.
		Thread scl = new Thread(new ServerConnectionListener(new ServerSocket(port), connections, timestamps));
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
