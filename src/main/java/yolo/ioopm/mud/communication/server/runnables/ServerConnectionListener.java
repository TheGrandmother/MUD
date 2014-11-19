package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectionListener implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerConnectionListener.class.getName());

	private final ServerSocket                  server_socket;
	private final Map<String, ClientConnection> connections;
	private final Map<String, Long>             timestamps;
	private final Queue<Message>                inbox;

	/**
	 * Listens for new connections on the given socket.
	 * Upon a new connection it starts an auxiliary thread that assures it is a legit connection
	 * and then adds it to the given connections and timestamps maps.
	 * Any messages received in that auxiliary thread is added to the inbox.
	 *
	 * @param socket - socket to listen on.
	 * @param connections - The map to add new connections to.
	 * @param timestamps - Map with the latest message timestamps.
	 * @param inbox - Inbox to add new messages to.
	 */
	public ServerConnectionListener(ServerSocket socket, Map<String, ClientConnection> connections, Map<String, Long> timestamps, Queue<Message> inbox) {
		this.server_socket = socket;
		this.connections = connections;
		this.timestamps = timestamps;
		this.inbox = inbox;
	}

	@Override
	public void run() {
		while(true) {
			try {
				logger.fine("Waiting for connection...");
				Socket socket = this.server_socket.accept();

				String ip = socket.getLocalAddress().toString();
				logger.fine("New connection: " + ip);

				logger.fine("Creating new ServerConnectionVerifier for new connection!");
				Thread scv = new Thread(new ServerConnectionVerifier(new ClientConnection(socket), connections, timestamps, inbox));
				scv.setName("ServerConnectionVerifier - IP: " + ip);
				scv.start();
			}
			catch(IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
