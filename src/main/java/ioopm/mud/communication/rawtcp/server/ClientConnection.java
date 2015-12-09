package ioopm.mud.communication.rawtcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection {

	private static final Logger logger = Logger.getLogger(ClientConnection.class.getName());

	private final Socket socket;
	private PrintWriter    print_writer    = null;
	private BufferedReader buffered_reader = null;

	/**
	 * Represents the connection a client has to the server.
	 * Keeps track of the socket and input/output streams.
	 *
	 * @param socket - The socket bound to the client.
	 * @throws IOException - If the constructor failed to create input/output streams for the socket.
	 */
	public ClientConnection(Socket socket) throws IOException {
		this.socket = socket;

		try {
			this.print_writer = new PrintWriter(socket.getOutputStream(), true);
			this.buffered_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, "Failed to create PrintWriter or BufferedReader for ip-address: " + socket.getLocalAddress().toString(), e);
			throw e;
		}
	}

	/**
	 * Writes the given string to the connection.
	 *
	 * @param message - The string to write.
	 */
	public synchronized void write(String message) {
		this.print_writer.println(message);
	}

	/**
	 * Checks if there is unread data in the input stream.
	 *
	 * @return - True if there is data to collect.
	 * @throws IOException - If an I/O error occurs.
	 */
	public synchronized boolean hasUnreadData() throws IOException {
		return this.buffered_reader.ready();
	}

	/**
	 * Reads a line from the input stream.
	 *
	 * @return - The string read.
	 * @throws IOException - IF an I/O error occurs.
	 */
	public synchronized String readLine() throws IOException {
		return this.buffered_reader.readLine();
	}

	/**
	 * Closes the socket for this connection.
	 *
	 * @throws IOException - If an I/O error occurs when closing the socket.
	 */
	public synchronized void killSocket() throws IOException {
		this.socket.close();
	}

	/**
	 * Retrieves the IP-address of the client bound to this connection.
	 *
	 * @return - The IP-address represented as a string.
	 */
	public synchronized String getIPAddress() {
		return socket.getRemoteSocketAddress().toString();
	}
}
