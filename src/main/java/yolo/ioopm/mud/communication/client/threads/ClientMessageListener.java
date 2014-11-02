package yolo.ioopm.mud.communication.client.threads;

import yolo.ioopm.mud.communication.Mailbox;
import yolo.ioopm.mud.communication.Message;

import java.io.BufferedReader;

public class ClientMessageListener extends Thread {

	private final BufferedReader br;
	private final Mailbox<Message> inbox;

	public ClientMessageListener(BufferedReader br, Mailbox<Message> inbox) {
		this.br = br;
		this.inbox = inbox;
	}

	@Override
	public void run() {

	}
}
