package ioopm.mud.communication.websocket;

import org.java_websocket.WebSocket;

public class WSClientConnection {

	private final WebSocket connection;
	private long latest_message = System.currentTimeMillis();

	public WSClientConnection(WebSocket con) {
		this.connection = con;
	}

	public void updateTimestamp(long timestamp) {
		this.latest_message = timestamp;
	}

	public long timeSinceLatestMessage() {
		return System.currentTimeMillis() - latest_message;
	}

	public WebSocket getSocket() {
		return connection;
	}
}
