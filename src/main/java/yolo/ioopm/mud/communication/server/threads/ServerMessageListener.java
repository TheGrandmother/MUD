package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ClientConnection;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerMessageListener extends Thread {

    private final Set<ClientConnection> connections;
    private final ConcurrentLinkedQueue<Message> inbox;

    public ServerMessageListener(Set<ClientConnection> connections, ConcurrentLinkedQueue<Message> inbox) {
        this.connections = connections;
        this.inbox = inbox;
    }

    @Override
    public void run() {
        //TODO sleep the thread after every iteration over the box
    }
}
