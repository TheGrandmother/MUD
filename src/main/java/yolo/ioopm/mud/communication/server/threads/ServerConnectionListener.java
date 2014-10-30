package yolo.ioopm.mud.communication.server.threads;

import yolo.ioopm.mud.communication.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConnectionListener extends Thread {

	private final ServerSocket                                server_socket;
	private final ConcurrentHashMap<String, ClientConnection> connections;

	public ServerConnectionListener(ServerSocket socket, ConcurrentHashMap<String, ClientConnection> connections) {
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
