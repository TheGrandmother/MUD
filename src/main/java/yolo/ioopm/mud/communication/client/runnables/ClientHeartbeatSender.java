package yolo.ioopm.mud.communication.client.runnables;

import yolo.ioopm.mud.communication.messages.ReplyMessage;

import java.io.PrintWriter;

public class ClientHeartbeatSender extends Thread {

	private final PrintWriter pw;
	private final String USERNAME;

	public ClientHeartbeatSender(PrintWriter pw, String username) {
		this.pw = pw;
		this.USERNAME = username;
	}

	@Override
	public void run() {
		while(true) {

			try {
				Thread.sleep(5000);
			}
			catch(InterruptedException e) {
				//TODO unhandled exception
				e.printStackTrace();
			}

			synchronized(pw) {
				pw.write(new ReplyMessage("server", USERNAME, "HeartBeat", System.currentTimeMillis(), null).getMessage());
			}
		}
	}
}
