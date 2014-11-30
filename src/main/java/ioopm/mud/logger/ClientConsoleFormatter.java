package ioopm.mud.logger;

import ioopm.mud.ui.ansi.AnsiCodes;
import ioopm.mud.ui.ansi.AnsiAttributeCodes;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * The most important formatter of the program.
 * Used to format the console handler for the client.
 * Everything printed to the client "chat" will go through this.
 */
public class ClientConsoleFormatter extends Formatter {

	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		sb.append(AnsiCodes.CURSOR_STORE_POSITION);
		sb.append(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(1).setIntTwo(15));

		// Scroll up the buffer 15 lines
		for(int i = 0; i < 15; i++) {
			sb.append(AnsiCodes.BUFFER_MOVE_UP_ONE);
		}

		sb.append(getTime(record.getMillis()));

		String name = record.getLevel().getName();

		switch(name) {
			case "SEVERE":
				sb.append(AnsiAttributeCodes.RED_BACKGROUND);
				break;

			case "WARNING":
				sb.append(AnsiAttributeCodes.YELLOW_BACKGROUND);
				break;

			case "FINE":
			case "FINER":
			case "FINEST":
				sb.append(AnsiAttributeCodes.MAGENTA_BACKGROUND);
				break;

			default:
				break;
		}

		if(!name.equals("INFO")) {
			sb.append(name).append(AnsiAttributeCodes.RESET_ATTRIBUTES).append(": ");
		}

		sb.append(record.getMessage());

		sb.append(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
		sb.append(AnsiCodes.CURSOR_RESTORE_POSITION);

		return sb.toString();
	}

	/**
	 * Worlds worst function
	 * @SuppressWarnings("deprecation")
	 * @param millis
	 * @return
	 */
	private static String getTime(long millis){
		Date d = new Date(millis);
		int hours = d.getHours();
		int minutes = d.getMinutes();
		int seconds = d.getSeconds();
		String lol = (hours>=10) ? hours+":" : "0"+hours+":";
		lol = (minutes>=10) ? lol+minutes+":" : lol+"0"+minutes+":";
		lol = (seconds>10) ? lol+seconds+"" : lol+"0"+seconds+"";

		return "\u001B[1m["+lol +"]\u001B[22m ";
	}
}
