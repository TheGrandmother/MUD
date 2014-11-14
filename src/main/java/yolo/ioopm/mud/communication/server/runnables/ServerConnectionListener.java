package yolo.ioopm.mud.communication.server.runnables;

import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ServerConnectionListener implements Runnable {

	private final ServerSocket                  server_socket;
	private final Map<String, ClientConnection> connections;
	private final Map<String, Long>             timestamps;

	public ServerConnectionListener(ServerSocket socket, Map<String, ClientConnection> connections, Map<String, Long> timestamps) {
		this.server_socket = socket;
		this.connections = connections;
		this.timestamps = timestamps;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Socket socket = this.server_socket.accept();

				String ip = socket.getLocalAddress().toString();
				System.out.println("New connection: " + ip);

				Thread scv = new Thread(new ServerConnectionVerifier(new ClientConnection(socket), connections, timestamps));
				scv.setName("ServerConnectionVerifier - IP: " + ip);
				scv.start();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
