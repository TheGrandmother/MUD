package ioopm.mud.communication.websocket;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;
import ioopm.mud.communication.messages.client.LogoutMessage;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.HandshakeReplyMessage;
import ioopm.mud.communication.messages.server.HeartbeatReplyMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
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

	private final Queue<Message> inbox = new ConcurrentLinkedDeque<>();
	private final Map<String, WSClientConnection> legit_connections = new ConcurrentHashMap<>();

	public WSServerAdapter(int port) {
		super(new InetSocketAddress(port));
		logger.info("WSServerAdapter initiated!");

		// Thread that checks for dead clients
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						// Allow at least three heartbeats to be sent to the server before we recheck the clients
						Thread.sleep(HEARTBEAT_FREQUENCY * 3);
					} catch(InterruptedException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}

					// Check for dead clients
					HashSet<String> dead_clients = null;
					for(Map.Entry<String, WSClientConnection> entry : legit_connections.entrySet()) {
						if(entry.getValue().timeSinceLatestMessage() > TIMEOUT_SECONDS) {

							logger.info("Connection \"" + entry.getKey() + "\" has timed out!");

							if(dead_clients == null) {
								dead_clients = new HashSet<String>();
							}

							dead_clients.add(entry.getKey());
						}
					}

					// Remove dead clients
					if(dead_clients != null) {
						logger.info("Removing " + dead_clients.size() + " timed out clients!");

						for(String dead : dead_clients) {
							// Close socket and remove from legit connections
							legit_connections.get(dead).getSocket().close();
							legit_connections.remove(dead);

							// Notify the game
							inbox.add(new LogoutMessage(dead));
						}
					}
				}
			}
		}).start();
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		logger.info("A new connection has been established! IP: " + getIP(conn));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		logger.info("A connection has been terminated! IP: " + getIP(conn));

		String to_remove = null;
		for(Map.Entry<String, WSClientConnection> entry : legit_connections.entrySet()) {
			if(entry.getValue().getSocket() == conn) {
				to_remove = entry.getKey();
				break;
			}
		}

		if(to_remove != null) {
			logger.info("Removing " + to_remove + " from legit connections!");
			legit_connections.remove(to_remove);

			// Notify the game engine about the dropped connection
			inbox.add(new LogoutMessage(to_remove));
		}
		else {
			logger.warning("Could not find legit-name for closed connection!");
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
    

		Message msg;
		try {
			msg = Message.deconstructTransmission(message);
      
      if(msg.getType() != MessageType.HEARTBEAT){
        logger.info("IP: " + getIP(conn) + " sent message: " + msg.toHumanForm());
      }

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
					legit_connections.put(sender, new WSClientConnection(conn));
					conn.send(new HandshakeReplyMessage(true, "Welcome to the server!").toString());
				}

				break;

			case LOGOUT:
				// This will call the onClose() method.
				conn.close();
				break;

			case HEARTBEAT:
				if(legit_connections.containsKey(msg.getSender())) {
					legit_connections.get(msg.getSender()).updateTimestamp(msg.getTimeStamp());
					conn.send(new HeartbeatReplyMessage(msg.getSender()).toString());
				}
				else {
					logger.warning("Non legit connection sent heartbeat! Sender: " + msg.getSender());
				}
				break;

			default:
				// Update the time of latest message for this connection
				legit_connections.get(msg.getSender()).updateTimestamp(msg.getTimeStamp());

				// Send the message to the game engine
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
			WebSocket conn = legit_connections.get(receiver).getSocket();

			if(conn.isClosed() || conn.isClosing()) {
				//TODO perform this "check" inside the onClose() method instead.
				logger.warning("Connection to user \"" + receiver + "\" was apparently closed! Removing user from server...");
				legit_connections.remove(receiver);
				inbox.add(new LogoutMessage(receiver));
			}
			else {
        if(m.getType() != MessageType.HEARTBEAT_REPLY){
			  	logger.fine("Attempting to send message: " + m.toHumanForm());
        }
				conn.send(m.toString());
			}
		} else {
			logger.warning("Tried to send message to non legit connection! Receiver: " + receiver + ", message: " + m.toString());
		}
	}
}
