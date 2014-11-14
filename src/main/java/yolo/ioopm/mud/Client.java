package yolo.ioopm.mud;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.client.ClientAdapter;
import yolo.ioopm.mud.communication.messages.client.AuthenticationMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

	private enum MenuItem {
		CONNECT(1);

		private final int INDEX;

		private MenuItem(int index) {
			INDEX = index;
		}

		public int getIndex() {
			return INDEX;
		}

		public static MenuItem getFromIndex(int index) {
			for(MenuItem item : values()) {
				if(item.getIndex() == index) {
					return item;
				}
			}

			return null;
		}
	}

	//TODO read these values from user
	private String username = null;
	private String password = null;

	private final BufferedReader keyboard_reader;

	private Adapter adapter = null;

	public Client() {
		AnsiConsole.systemInstall();
		keyboard_reader = new BufferedReader(new InputStreamReader(System.in));

		run();
	}

	private void run() {
		while(true) {
			switch(retreiveMenuChoice()) {
				case CONNECT:
					connect();
					break;
			}
		}
	}

	private void connect() {
		username = getUsername();
		password = getPassword();

		if(username == null || password == null) {
			System.out.println("Something went wrong when retrieving username and/or password!");
			return;
		}

		String host = "localhost";
		int port = 1337;
		try {
			adapter = new ClientAdapter(host, port, username);
		}
		catch(IOException e) {
			System.out.println("Failed to create ClientAdapter!");
			e.printStackTrace();
			return;
		}

		if(authenticate(username, password)) {
			System.out.println("You successfully authenticated yourself!");
		}
		else {
			System.out.println("Failed to authenticate against server! Is the name is use or are the details incorrect?");
			return;
		}
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
				case "false":
					return false;
				case "true":
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

	private String getUsername() {
		StringBuilder sb = new StringBuilder();

		sb.append(Ansi.ansi().eraseScreen());
//		sb.append(Ansi.ansi().cursor(0,0));
		sb.append("Please enter your username:");

		AnsiConsole.out.println(sb);

		String username;
		try {
			username = keyboard_reader.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		return username;
	}

	private String getPassword() {
		StringBuilder sb = new StringBuilder();

		sb.append(Ansi.ansi().eraseScreen());
//		sb.append(Ansi.ansi().cursor(0,0));
		sb.append("Please enter your password:");

		AnsiConsole.out.println(sb);

		String password;
		try {
			password = keyboard_reader.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		return password;
	}

	private MenuItem retreiveMenuChoice() {
		StringBuilder sb = new StringBuilder();

//		sb.append(Ansi.ansi().eraseScreen());
//		sb.append(Ansi.ansi().cursor(0,0));
//		sb.append(Ansi.ansi().cursor(1,0));
		sb.append(Ansi.ansi().bg(Ansi.Color.GREEN));
		sb.append("^^^^^^^^^^ INPUT ^^^^^^^^^^");
		sb.append(Ansi.ansi().bg(Ansi.Color.DEFAULT));
//		sb.append(Ansi.ansi().cursor(2,0));

		for(MenuItem item : MenuItem.values()) {
			sb.append(item.getIndex() + ". " + item.name() + "\n");
		}

//		sb.append(Ansi.ansi().cursor(0,0));

		AnsiConsole.out.print(sb.toString());

		String choice;
		try {
			choice = keyboard_reader.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		int index;
		try {
			index = Integer.valueOf(choice);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

		return MenuItem.getFromIndex(index);
	}
}
