package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class ServerConnectionListener extends Thread {

    private final ServerSocket          server_socket;
    private final Set<ClientConnection> connections;

    public ServerConnectionListener(ServerSocket socket, Set<ClientConnection> connections) {
        this.server_socket = socket;
        this.connections = connections;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket socket = this.server_socket.accept();

                String ip = socket.getLocalAddress().toString();
                System.out.println("New connection: " + ip);

                connections.add(new ClientConnection(socket));
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
