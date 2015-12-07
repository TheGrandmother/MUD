package ioopm.mud.communication.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initiates a WebSocket server that listens for WebSocket connections and handles them.
 * The functionality of this object is the same as for the TCPServerAdapter, but it works over WebSockets
 * instead of raw TCP sockets (duh).
 *
 *
 * It also supports SSL secured communication thanks to WSS.
 * This requires the host to have a valid certificate to work properly.
 */
public class WSServerAdapter extends WebSocketServer {

    private static final Logger logger = Logger.getLogger(WSServerAdapter.class.getName());

    public WSServerAdapter(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("A new connection has been established! IP: " + getIP(conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("A connection has been terminated! IP: " + getIP(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info("IP: " + getIP(conn) + " sent message: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.warning("IP: " + getIP(conn) + " cause an exception!");
        logger.log(Level.WARNING, ex.getMessage(), ex);
    }

    private String getIP(WebSocket conn) {
        return conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }
}
