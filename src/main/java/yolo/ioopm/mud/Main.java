package yolo.ioopm.mud;

import yolo.ioopm.mud.logger.ClientConsoleFormatter;
import yolo.ioopm.mud.logger.HTMLFormatter;
import yolo.ioopm.mud.logger.ServerConsoleFormatter;

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
				try {
					// TODO change Level.ALL to Level.INFO to disable debugging output to client
					setConsoleFormatter(root_logger, new ClientConsoleFormatter(), Level.INFO);
					new Client();
				}
				catch(Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
				break;

			case "server":
				logger.fine("Initiating server...");
				try {
					if(args.length != 2){
						System.out.println("Nowadays you have to type the path to the world files");
						throw new Exception();
					}

					//TODO change Level.ALL to Level.INFO to disable debug output to server
					setConsoleFormatter(root_logger, new ServerConsoleFormatter(), Level.ALL);
					new Server(args[1].trim());
				}
				catch(Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
				break;
		}
	}

	private static void setConsoleFormatter(Logger logger, Formatter formatter, Level level) {
		Handler[] handlers = logger.getHandlers();
		for(Handler h : handlers) {
			if(h instanceof ConsoleHandler) {
				h.setFormatter(formatter);
				h.setLevel(level);
			}
		}
	}
}
