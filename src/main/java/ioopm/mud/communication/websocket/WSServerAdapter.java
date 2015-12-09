package ioopm.mud.communication.websocket;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.HandshakeReplyMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initiates a WebSocket server that listens for WebSocket connections and handles them.
 * The functionality of this object is the same as for the TCPServerAdapter, but it works over WebSockets
 * instead of raw TCP sockets (duh).
 * <p>
 * <p>
 * It also supports SSL secured communication thanks to WSS.
 * This requires the host to have a valid certificate to work properly.
 */
public class WSServerAdapter extends WebSocketServer implements Adapter {

	private static final Logger logger = Logger.getLogger(WSServerAdapter.class.getName());

	private final Queue<Message> inbox = new ArrayDeque<>();
	private final Map<String, WebSocket> legit_connections = new HashMap<>();

	public WSServerAdapter(int port) {
		super(new InetSocketAddress(port));
		logger.info("WSServerAdapter initiated!");
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		logger.info("A new connection has been established! IP: " + getIP(conn));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		logger.info("A connection has been terminated! IP: " + getIP(conn));

		String to_remove = null;
		for(Map.Entry<String, WebSocket> entry : legit_connections.entrySet()) {
			if(entry.getValue() == conn) {
				to_remove = entry.getKey();
			}
		}

		if(to_remove != null) {
			logger.info("Removing " + to_remove + " from legit connections!");
			legit_connections.remove(to_remove);
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		logger.info("IP: " + getIP(conn) + " sent message: " + message);

		Message msg;
		try {
			msg = Message.deconstructTransmission(message);
		} catch(IllegalArgumentException e) {
			logger.warning("Received malformed message! Message: " + message);
			logger.log(Level.FINE, e.getMessage(), e);

			conn.send(new ErrorMessage("foo", "Malformed message!").toString());

			return;
		}

		switch(msg.getType()) {
			case HANDSHAKE:
				String sender = msg.getSender();

				if(legit_connections.containsKey(sender)) {
					conn.send(new HandshakeReplyMessage(false, "There is already a user with that name connected!").toString());
				} else {
					legit_connections.put(sender, conn);
					conn.send(new HandshakeReplyMessage(true, "Welcome to the server!").toString());
				}

				break;

			case LOGOUT:
				legit_connections.remove(msg.getSender());
			default:
				inbox.add(msg);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		if(conn != null) {
			logger.warning("IP: " + getIP(conn) + " caused an exception!");
		}

		logger.log(Level.WARNING, ex.getMessage(), ex);
	}

	private String getIP(WebSocket conn) {
		return conn.getRemoteSocketAddress().getAddress().getHostAddress();
	}

	@Override
	public Message poll() {
		return inbox.poll();
	}

	@Override
	public void sendMessage(Message m) {
		String receiver = m.getReceiver();

		if(legit_connections.containsKey(receiver)) {
			WebSocket conn = legit_connections.get(receiver);
			conn.send(m.toString());
		} else {
			logger.warning("Tried to send message to non legit connection! Receiver: " + receiver + ", message: " + m.toString());
		}
	}
}
