package ioopm.mud.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Used to format the ConsoleHandler for the logger on the server.
 */
public class ServerConsoleFormatter extends Formatter {

	/**
	 * Formats the given record.
	 *
	 * @param record - Record to format.
	 * @return - String representing the formatted record.
	 */
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		// Date
		Date d = new Date(record.getMillis());
		sb.append('[');
		sb.append(d.getHours()).append(':');
		sb.append(d.getMinutes()).append(':');
		sb.append(d.getSeconds());
		sb.append(']');

		// Level
		sb.append('[').append(record.getLevel().getName()).append(']');

		// Message
		sb.append(' ').append(record.getMessage());

		// StackTrace
		if(record.getThrown() != null) {
			sb.append("\nStack trace:\n");
			for(StackTraceElement s : record.getThrown().getStackTrace()) {
				sb.append(s).append('\n');
			}
		}

		// New line
		sb.append('\n');

		return sb.toString();
	}
}
