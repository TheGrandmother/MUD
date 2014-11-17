package yolo.ioopm.mud;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {

		FileHandler log_file;
		try {
			log_file = new FileHandler("MUD-log.txt");
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}

		Logger root_logger = Logger.getLogger("");
		root_logger.addHandler(log_file);
		root_logger.addHandler(new ConsoleHandler());
		root_logger.setLevel(Level.INFO);

		Logger communication_logger = Logger.getLogger("yolo.ioopm.mud.communication");
		communication_logger.setLevel(Level.ALL);

		logger.fine("Program has been started!");

		switch(args[0].toLowerCase()) {
			case "client":
				logger.fine("Initiating client...");
				new Client();
				break;
			case "server":
				logger.fine("Initiating server...");
				new Server();
				break;
		}
	}
}
