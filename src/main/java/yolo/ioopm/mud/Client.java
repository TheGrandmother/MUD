package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.AuthenticationMessage;

import java.io.IOException;

public class Client {

	//TODO read these values from user
	private final String USERNAME = "Player";
	private final String PASSWORD = "asdf1234";

	private Adapter adapter = null;

	public Client(String host, int port) {
		try {
			adapter = new ClientAdapter(host, port, USERNAME);
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		authenticate(USERNAME, PASSWORD);
	}

	private boolean authenticate(String username, String password) {
		// Authenticate against server
		Message msg = new AuthenticationMessage(username, password);
		adapter.sendMessage(msg);

		// Poll adapter every 0.2 seconds until we receive an answer.
		Message answer;
		while((answer = adapter.poll()) == null) {
			try {
				Thread.sleep(200);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(answer.getType() == MessageType.AUTHENTICATION_REPLY) {
			switch(answer.getArguments()[0]) {
				case "incorrectlogin":
					System.out.println("You entered the wrong details!");
					return false;
				case "successfulllogin":
					System.out.println("You successfully authenticated yourself!");
					return true;
				default:
					System.out.println("Received unexpected message! Message: \"" + answer.getMessage() + "\"");
					return false;
			}
		}
		else {
			System.out.println("Received incorrect message! Message: \"" + answer.getMessage() + "\"");
			return false;
		}
	}
}
