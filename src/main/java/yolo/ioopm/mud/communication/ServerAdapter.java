package yolo.ioopm.mud.communication;

import yolo.ioopm.mud.communication.threads.ServerConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAdapter implements Adapter {

    ConcurrentHashMap<Socket, Object[]> connections = new ConcurrentHashMap<>();

    public ServerAdapter(int port) throws IOException {

        // Async thread - listens for new connections and adds them to the connections-map
        new ServerConnectionListener(new ServerSocket(port), connections).start();
    }

    @Override
    public void sendMessage(Message message) throws CommunicationError {

    }

    @Override
    public Message pollForMessage() throws CommunicationError {
        return null;
    }
}
