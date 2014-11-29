package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMessageSender implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerMessageSender.class.getName());

	private final Map<String, ClientConnection> connections;
	private final Queue<Message>                outbox;

	/**
	 * Iterates over the given outbox and sends messages to the clients in
	 * the given connections map.
	 *
	 * @param connections - Verified client connections, mapped by their usernames.
	 * @param outbox - The outbox to iterate over.
	 */
	public ServerMessageSender(Map<String, ClientConnection> connections, Queue<Message> outbox) {
		this.connections = connections;
		this.outbox = outbox;
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

			Message msg;
			while((msg = outbox.poll()) != null) {
				if(msg.getType() != MessageType.HEARTBEAT_REPLY) {
					logger.fine("Sending message: \"" + msg.getMessage() + "\"");
				}
				ClientConnection cc = connections.get(msg.getReceiver());

				if(cc == null) {
					logger.severe("Call to connections.get resulted in null! msg.receiver(): \"" + msg.getReceiver() + "\"");
					continue;
				}

				cc.write(msg.getMessage());
			}
		}
	}
}
