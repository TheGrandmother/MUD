package ioopm.mud.communication.rawtcp.client;

import ioopm.mud.communication.rawtcp.Adapter;
import ioopm.mud.communication.rawtcp.client.runnables.ClientHeartbeatSender;
import ioopm.mud.communication.rawtcp.client.runnables.ClientMessageListener;
import ioopm.mud.communication.rawtcp.client.runnables.ClientMessageSender;
import ioopm.mud.communication.messages.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ClientAdapter extends Adapter {

	private static final Logger logger = Logger.getLogger(ClientAdapter.class.getName());

	private final PrintWriter print_writer;

	/**
	 * Attempts to connect to the server on the given host-address and port.
	 * Then starts the necessary threads that listens/sends messages to/from the client.
	 *
	 * @param host - Host address to connect to.
	 * @param port - Port on the host.
	 * @param username - Username to sign heartbeats with.
	 *
	 * @throws UnknownHostException - if the IP address of the host could not be determined.
	 * @throws IOException - if an I/O error occurs when creating the socket.
	 * @throws IllegalArgumentException - if a security manager exists and its checkConnect method doesn't allow the operation.
	 * @throws SecurityException - if the port parameter is outside the specified range of valid port values, which is between 0 and 65535, inclusive.
	 */
	public ClientAdapter(String host, int port, String username)
			throws UnknownHostException, IOException, IllegalArgumentException, SecurityException {

		logger.fine("Attempting to bind socket!");
		Socket socket = new Socket(host, port);

		// Access to these two objects need to be synchronized
		print_writer = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		logger.fine("Socket and in/out-streams bound successfully!");

		logger.fine("Initiating threads!");
		Thread cms = new Thread(new ClientMessageSender(print_writer, outbox));
		cms.setName("ClientMessageSender");
		cms.start();

		Thread cml = new Thread(new ClientMessageListener(br, inbox));
		cml.setName("ClientMessageListener");
		cml.start();

		Thread chs = new Thread(new ClientHeartbeatSender(outbox, username));
		chs.setName("ClientHeartbeatSender");
		chs.start();

		logger.fine("Startup sequence finished!");
	}

	/**
	 * Ignores the message queue and immediately attempts to send the message to the server.
	 * @param msg - The message to send.
	 */
	public void forceSendMessage(Message msg) {
		synchronized(print_writer) {
			print_writer.write(msg.getMessage());
			print_writer.flush();
		}
	}
}
