package yolo.ioopm.mud.communication.client.runnables;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.client.HeartBeatMessage;

import java.io.PrintWriter;

public class ClientHeartbeatSender implements Runnable {

	private final PrintWriter pw;
	private final String      USERNAME;

	public ClientHeartbeatSender(PrintWriter pw, String username) {
		this.pw = pw;
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

			synchronized(pw) {
				pw.write(new HeartBeatMessage(USERNAME).getMessage());
			}
		}
	}
}
