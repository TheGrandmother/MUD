package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ServerConnectionListener implements Runnable {

	private final ServerSocket                  server_socket;
	private final Map<String, ClientConnection> connections;

	public ServerConnectionListener(ServerSocket socket, Map<String, ClientConnection> connections) {
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

				new Thread(new ServerConnectionVerifier(new ClientConnection(socket), connections)).start();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
