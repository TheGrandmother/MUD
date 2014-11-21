package yolo.ioopm.mud.userinterface;

import yolo.ioopm.mud.Client;
import yolo.ioopm.mud.ansi.GeneralAnsiCodes;
import yolo.ioopm.mud.communication.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientInterface {

	private static final Logger logger = Logger.getLogger(ClientInterface.class.getName());

	private final Client client;

	private final PrintStream    out;
	private final BufferedReader in;

	public ClientInterface(Client instance, PrintStream out, BufferedReader in) {
		this.client = instance;
		this.out = out;
		this.in = in;
	}

	public void run() {

		formatTerminal();

		new Thread(
			new Runnable() {
				@Override
				public void run() {
					while(true) {
						Message msg = client.pollMessage();
						printToOut(formatMessage(msg));
					}
				}
			}
		).start();

		MenuItem choice;
		while(true) {
			String input = prompt(getMenuQuestion());

			try {
				choice = MenuItem.valueOf(input.toUpperCase());
				break;
			}
			catch(IllegalArgumentException e) {
				printToOut("Incorrect choice! Please try again!");
			}
		}

		switch(choice) {
			case LOGIN:
				try {
					client.authenticate();
				}
				catch(IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
				break;

			case REGISTER:
				try {
					client.register();
				}
				catch(IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
				break;
		}
	}

	private void formatTerminal() {
		synchronized(out) {
			out.print(GeneralAnsiCodes.CLEAR_SCREEN);
			out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(1).setIntTwo(1));
			out.print("Welcome to MUD!");
			out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(16).setIntTwo(1));
			out.print("What would you like to do?");
			out.print(GeneralAnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
			out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(18).setIntTwo(1));
		}
	}

	private String getMenuQuestion() {
		StringBuilder sb = new StringBuilder();
		for(MenuItem i : MenuItem.values()) {
			sb.append(i.name() + " ");
		}
		return sb.toString();
	}

	private String formatMessage(Message msg) {
		return msg.getMessage();
	}

	public void printToOut(String output) {
		synchronized(out) {
			out.print(GeneralAnsiCodes.CURSOR_STORE_POSITION);
			out.print(GeneralAnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(1).setIntTwo(15));

			// Scroll up the buffer 15 lines
			for(int i = 0; i < 15; i++) {
				out.print(GeneralAnsiCodes.BUFFER_MOVE_UP_ONE);
			}

			out.print(output);
			out.print(GeneralAnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
			out.print(GeneralAnsiCodes.CURSOR_RESTORE_POSITION);
		}
	}

	public String prompt(String question) {

		synchronized(out) {
			out.print(GeneralAnsiCodes.CURSOR_STORE_POSITION);
			out.print(GeneralAnsiCodes.CURSOR_SET_POSITION.setIntOne(17).setIntTwo(1));
			out.print(GeneralAnsiCodes.CLEAR_LINE);
			out.print(question);
			out.print(GeneralAnsiCodes.CURSOR_RESTORE_POSITION);
		}

		String input;
		try {
			input = in.readLine();
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}

		return input;
	}
}
