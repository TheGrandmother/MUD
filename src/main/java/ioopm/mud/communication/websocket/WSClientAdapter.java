package ioopm.mud.communication.websocket;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WSClientAdapter extends WebSocketClient implements Adapter {

    private static final Logger logger = Logger.getLogger(WSClientAdapter.class.getName());

    public WSClientAdapter(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Established a connection to the given URI :)");
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.warning("The connection to the server has been closed!");
    }

    @Override
    public void onError(Exception ex) {
        logger.log(Level.WARNING, ex.getMessage(), ex);
    }

    @Override
    public Message poll() {
        return null;
    }

    @Override
    public void sendMessage(Message m) {
        this.send(m.getMessage());
    }
}
