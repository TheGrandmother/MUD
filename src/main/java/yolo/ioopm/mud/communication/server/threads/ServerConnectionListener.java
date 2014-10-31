package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConnectionListener extends Thread {

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

				final ClientConnection connection = new ClientConnection(socket);

				// New thread waits for the user to provide a username.
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							connection.write("Please enter your username:");

							String username = null;
							while(connection.isAlive()) {

								//TODO add timeout on sockets
								String temp = connection.readLine(); // This line will block this thread until it can read something

								if(connections.containsKey(temp)) {
									connection.write("That username is already taken! Please provide a new one:");
								}
								else {
									username = temp;
									break;
								}
							}

							if(username != null) {
								connections.put(username, connection);
								timestamps.put(username, System.currentTimeMillis());
								System.out.println("New user chose name: \"" + username + "\"");
							}
							else {
								System.out.println("Error! New user gave null username! Did the connection close?");
							}
						}
						catch(IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
