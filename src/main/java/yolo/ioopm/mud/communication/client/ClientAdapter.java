package yolo.ioopm.mud.communication.client;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.client.runnables.ClientHeartbeatSender;
import yolo.ioopm.mud.communication.client.runnables.ClientMessageListener;
import yolo.ioopm.mud.communication.client.runnables.ClientMessageSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ClientAdapter extends Adapter {

	private static final Logger logger = Logger.getLogger(ClientAdapter.class.getName());

	public ClientAdapter(String host, int port, String username)
			throws UnknownHostException, IOException, IllegalArgumentException, SecurityException {

		logger.fine("Attempting to bind socket!");
		Socket socket = new Socket(host, port);

		// Access to these two objects need to be synchronized
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		logger.fine("Socket and in/out-streams bound successfully!");

		logger.fine("Initiating threads!");
		Thread cms = new Thread(new ClientMessageSender(pw, outbox));
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
}
