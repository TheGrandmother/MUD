package yolo.ioopm.mud.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class HTMLFormatter extends Formatter {

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();

		builder.append("<tr>");
		builder.append("<td>").append(record.getLevel()).append("</td>");
		builder.append("<td>").append((new Date(record.getMillis())).toString()).append("</td>");
		builder.append("<td>").append(record.getLoggerName()).append("</td>");
		builder.append("<td>").append(record.getSourceMethodName()).append("</td>");
		builder.append("<td>").append(record.getMessage()).append("</td>");

		builder.append("<td>");
		if(record.getThrown() != null) {
			for(StackTraceElement trace : record.getThrown().getStackTrace()) {
				builder.append(trace).append("</br>");
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