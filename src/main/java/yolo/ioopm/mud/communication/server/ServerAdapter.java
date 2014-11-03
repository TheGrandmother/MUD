package yolo.ioopm.mud.communication.server;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.server.runnables.ServerConnectionListener;
import yolo.ioopm.mud.communication.server.runnables.ServerMessageListener;
import yolo.ioopm.mud.communication.server.runnables.ServerMessageSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAdapter extends Adapter {

	private final ConcurrentHashMap<String, ClientConnection> connections = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Long>             timestamps  = new ConcurrentHashMap<>();

	public ServerAdapter(int port) throws IOException, SecurityException, IllegalArgumentException {

		// Async thread - Listens for new connections and adds them to connections.
		new Thread(new ServerConnectionListener(new ServerSocket(port), connections)).start();

		// Async thread - Listens for new messages from the clients.
		new Thread(new ServerMessageListener(connections, inbox, timestamps)).start();

		// Async thread - Sends the messages that are currently in the outbox.
		new Thread(new ServerMessageSender(connections, outbox)).start();
	}
}
