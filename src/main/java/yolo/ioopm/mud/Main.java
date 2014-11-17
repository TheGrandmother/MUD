package yolo.ioopm.mud;

import yolo.ioopm.mud.misc.HTMLFormatter;

import java.io.IOException;
import java.util.logging.*;

public class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {

		Logger root_logger = Logger.getLogger("");
		root_logger.setLevel(Level.ALL);

		FileHandler log_file;
		try {
			log_file = new FileHandler("MUD-log.html");
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}

		// Log everything to log file
		log_file.setFormatter(new HTMLFormatter());
		log_file.setLevel(Level.ALL);

		root_logger.addHandler(log_file);

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
