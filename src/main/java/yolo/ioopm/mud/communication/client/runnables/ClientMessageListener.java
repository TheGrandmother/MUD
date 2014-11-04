package yolo.ioopm.mud.communication.client.runnables;

import yolo.ioopm.mud.communication.Message;

import java.io.BufferedReader;
import java.util.Queue;

public class ClientMessageListener implements Runnable {

	private final BufferedReader br;
	private final Queue<Message> inbox;

	public ClientMessageListener(BufferedReader br, Queue<Message> inbox) {
		this.br = br;
		this.inbox = inbox;
	}

	@Override
	public void run() {

	}
}
