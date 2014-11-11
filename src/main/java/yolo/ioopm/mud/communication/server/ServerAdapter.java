package yolo.ioopm.mud.communication.server;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.server.runnables.ServerConnectionListener;
import yolo.ioopm.mud.communication.server.runnables.ServerMessageListener;
import yolo.ioopm.mud.communication.server.runnables.ServerMessageSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAdapter extends Adapter {

	private final Map<String, ClientConnection> connections = new ConcurrentHashMap<>();
	private final Map<String, Long>             timestamps  = new ConcurrentHashMap<>();

	public ServerAdapter(int port) throws IOException, SecurityException, IllegalArgumentException {

		// Async thread - Listens for new connections and adds them to connections.
		new Thread(new ServerConnectionListener(new ServerSocket(port), connections, timestamps)).start();

		// Async thread - Listens for new messages from the clients.
		// Needs outbox to be able to reply to hearbeats.
		new Thread(new ServerMessageListener(connections, inbox, outbox, timestamps)).start();

		// Async thread - Sends the messages that are currently in the outbox.
		new Thread(new ServerMessageSender(connections, outbox)).start();
	}
}
