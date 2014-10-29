package yolo.ioopm.mud.communication.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConnectionListener extends Thread {

    private final ServerSocket socket;
    private final ConcurrentHashMap<Socket, Object[]> connections;

    public ServerConnectionListener(ServerSocket socket, ConcurrentHashMap<Socket, Object[]> connection_map) {
        this.socket = socket;
        this.connections = connection_map;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket new_connection = socket.accept();

                String ip = new_connection.getLocalAddress().toString();
                System.out.println("New connection: " + ip);

                connections.put(
                    new_connection,
                    new Object[] {
                        new PrintWriter(new_connection.getOutputStream(), true),
                        new BufferedReader(new InputStreamReader(new_connection.getInputStream())),
                    }
                );
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
