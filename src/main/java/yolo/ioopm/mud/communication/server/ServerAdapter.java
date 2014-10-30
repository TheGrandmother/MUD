package yolo.ioopm.mud.communication.server;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.threads.ServerConnectionListener;
import yolo.ioopm.mud.communication.server.threads.ServerMessageListener;
import yolo.ioopm.mud.communication.server.threads.ServerMessageSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAdapter extends Adapter {

	private final Set<ClientConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<ClientConnection, Boolean>());

	public ServerAdapter(int port) throws IOException {

		// Async thread - Listens for new connections and adds them to the connections-set.
		new ServerConnectionListener(new ServerSocket(port), connections).start();

		// Async thread - Listens for new messages from the clients.
		new ServerMessageListener(connections, inbox).start();

		// Async thread - Sends the messages that are currently in the outbox.
		new ServerMessageSender(connections, outbox).start();
	}

	@Override
	public void sendMessage(final Message message) throws CommunicationError {

		// The adding is made in a new thread so the main thread isn't blocked if the outbox is currently locked.
		new Thread(new Runnable() {
			@Override
			public void run() {
				outbox.add(message);
			}
		}).start();
	}

	@Override
	public Message pollForMessage() throws CommunicationError {
		return null;
	}
}
