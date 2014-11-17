package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMessageSender implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerMessageSender.class.getName());

	private final Map<String, ClientConnection> connections;
	private final Queue<Message>                outbox;

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
				logger.fine("Sending message: \"" + msg.getMessage() + "\"");
				ClientConnection cc = connections.get(msg.getReceiver());
				cc.write(msg.getMessage());
			}
		}
	}
}
