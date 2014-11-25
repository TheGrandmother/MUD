package yolo.ioopm.mud.logger;

import yolo.ioopm.mud.ui.ansi.AnsiCodes;
import yolo.ioopm.mud.ui.ansi.AnsiColorCodes;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ClientConsoleFormatter extends Formatter {

	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		sb.append(AnsiCodes.CURSOR_STORE_POSITION);
		sb.append(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(1).setIntTwo(15));

		// Scroll up the buffer 15 lines
		for(int i = 0; i < 15; i++) {
			sb.append(AnsiCodes.BUFFER_MOVE_UP_ONE);
		}

		String name = record.getLevel().getName();

		switch(name) {
			case "SEVERE":
				sb.append(AnsiColorCodes.RED_BACKGROUND);
				break;

			case "WARNING":
				sb.append(AnsiColorCodes.YELLOW_BACKGROUND);
				break;

			case "FINE":
			case "FINER":
			case "FINEST":
				sb.append(AnsiColorCodes.MAGENTA_BACKGROUND);
				break;

			default:
				break;
		}

		if(!name.equals("INFO")) {
			sb.append(name).append(AnsiColorCodes.RESET_ATTRIBUTES).append(": ");
		}

		sb.append(record.getMessage());

		sb.append(AnsiCodes.BUFFER_SET_TOP_BOTTOM.setIntOne(18).setIntTwo(18));
		sb.append(AnsiCodes.CURSOR_RESTORE_POSITION);

		return sb.toString();
	}
}
