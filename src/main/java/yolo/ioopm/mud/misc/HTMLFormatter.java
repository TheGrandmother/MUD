package yolo.ioopm.mud.misc;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class HTMLFormatter extends Formatter {

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();

		builder.append("<tr>");
		builder.append("<td>" + record.getLevel() + "</td>");
		builder.append("<td>" + (new Date(record.getMillis())).toString() + "</td>");
		builder.append("<td>" + record.getLoggerName() + "</td>");
		builder.append("<td>" + record.getSourceMethodName() + "</td>");
		builder.append("<td>" + record.getMessage() + "</td>");

		builder.append("<td>");
		if(record.getThrown() != null) {
			for(StackTraceElement trace : record.getThrown().getStackTrace()) {
				builder.append(trace + "</br>");
			}
		}
		builder.append("</td>");

		builder.append("</tr>\n");

		return builder.toString();
	}

	public String getHead(Handler h) {
		return ("<html>\n <body>\n <Table border>\n<tr><td>Level</td><td>Time</td><td>Logger</td><td>Function</td><td>Log Message</td><td>Stack trace</td><</tr>\n");
	}

	public String getTail(Handler h) {
		return ("</table>\n</body>\n</html>");
	}
}