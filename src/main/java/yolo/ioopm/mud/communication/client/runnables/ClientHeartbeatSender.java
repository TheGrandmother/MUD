package yolo.ioopm.mud.communication.client.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.messages.client.HeartBeatMessage;

import java.io.PrintWriter;
import java.util.Queue;

public class ClientHeartbeatSender implements Runnable {

	private final Queue<Message> outbox;
	private final String         USERNAME;

	public ClientHeartbeatSender(Queue<Message> outbox, String username) {
		this.outbox = outbox;
		this.USERNAME = username;
	}

	@Override
	public void run() {
		while(true) {

			try {
				Thread.sleep(Adapter.HEARTBEAT_FREQUENCY);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			outbox.offer(new HeartBeatMessage(USERNAME));
		}
	}
}
