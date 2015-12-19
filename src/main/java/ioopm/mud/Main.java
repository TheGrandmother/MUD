package ioopm.mud;

import ioopm.mud.logger.HTMLFormatter;
import ioopm.mud.logger.ServerConsoleFormatter;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.*;

public class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	/**
	 * Initiates the program.
	 *
	 * @param args - Needs to contain at least one element, which can be either "client" or "server".
	 */
	public static void main(String[] args) {

		// Set VM-wide locale to swedish
		Locale.setDefault(Locale.forLanguageTag("SE"));

		// Set up the logger.
		setUpLogger();

		logger.fine("Initiating server...");

		try {
			setRootFormatter(new ServerConsoleFormatter(), Level.ALL);
			new Server();
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Sets up the logger.
	 * Creates log file and sets the general log level.
	 */
	private static void setUpLogger() {
		Logger root_logger = Logger.getLogger("");
		root_logger.setLevel(Level.ALL);

		FileHandler log_file;
		try {
			log_file = new FileHandler("MUD-log.html");
		} catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}

		// Log everything to log file
		log_file.setFormatter(new HTMLFormatter());
		log_file.setLevel(Level.ALL);

		root_logger.addHandler(log_file);
	}

	/**
	 * Sets the formatter for the ConsoleHandlers of the defined logger.
	 *
	 * @param formatter - The formatter to use on the ConsoleHandlers.
	 * @param level     - The level to log.
	 */
	private static void setRootFormatter(Formatter formatter, Level level) {
		Logger root = Logger.getLogger("");
		Handler[] handlers = root.getHandlers();
		for(Handler h : handlers) {
			if(h instanceof ConsoleHandler) {
				h.setFormatter(formatter);
				h.setLevel(level);
			}
		}
	}
}
