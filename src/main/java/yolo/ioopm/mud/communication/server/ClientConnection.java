package yolo.ioopm.mud.communication.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {

	private final Socket socket;
	private PrintWriter    print_writer    = null;
	private BufferedReader buffered_reader = null;

	public ClientConnection(Socket socket) {
		this.socket = socket;

		try {
			this.print_writer = new PrintWriter(socket.getOutputStream(), true);
			this.buffered_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void write(String message) {
		this.print_writer.println(message);
		this.print_writer.flush();
	}

	public synchronized boolean hasUnreadData() throws IOException {
		return this.buffered_reader.ready();
	}

	public synchronized String readLine() throws IOException {
		return this.buffered_reader.readLine();
	}

	public synchronized boolean isAlive() {
		return this.socket.isConnected() && !this.socket.isClosed();
	}

	public synchronized void killSocket() throws IOException {
		this.socket.close();
	}
}
