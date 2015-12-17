package ioopm.mud.communication.messages;

import java.util.Base64;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
/**
 * This is the class which specifies the messages. These messages are sent to the Adapter and from there
 * translated and sent to the server.
 * <p>
 * Each message is on the form<br>
 * <code>\<sender\>;\<receiver\>;\<action\>;\<time_stamp\>;\<argument1\>;\<argument2\>;.....;\<argumentN\></code>
 *
 * @author TheGrandmother
 */

public abstract class Message {

	private static final Logger logger = Logger.getLogger(Message.class.getName());

	/* Regex groups #:
		1 - Receiver  (base64 encoded)
		2 - Sender    (base64 encoded)
		3 - Type
		4 - Action
		5 - Timestamp
		6 - List of arguments (if any) (base64 encoded)
	 */
	private static final String MESSAGE_REGEX = "^([\\w+/=]+);([\\w+/=]+);(\\w+);(\\w+);(\\d+);((?:[\\w+/=]+;)*)\\s{0,2}$";
	private static final Pattern MESSAGE_PATTERN = Pattern.compile(MESSAGE_REGEX);

	private static final Base64.Encoder encoder = Base64.getEncoder();
	private static final Base64.Decoder decoder = Base64.getDecoder();

	private final String RECEIVER;
	private final String SENDER;
	private final MessageType TYPE;
	private final String ACTION;
	private final String[] ARGUMENTS;
	private final long TIME_STAMP;

	/**
	 * Creates a new message.
	 *
	 * @param receiver
	 * @param sender
	 * @param action
	 * @param arguments
	 */
	protected Message(String receiver, String sender, MessageType type, String action, String... arguments) {
		this(receiver, sender, type, action, System.currentTimeMillis(), arguments);
	}

	/**
	 * Creates a new message.
	 *
	 * @param receiver
	 * @param sender
	 * @param action
	 * @param arguments
	 */
	private Message(String receiver, String sender, MessageType type, String action, long time_stamp, String[] arguments) {
		this.RECEIVER = receiver;
		this.SENDER = sender;
		this.TYPE = type;
		this.ACTION = action;
		this.ARGUMENTS = arguments;
		this.TIME_STAMP = time_stamp;
	}

	/**
	 * This function takes a string received by the adapter and converts it to a Message object.
	 *
	 * @param transmission - The data to convert.
	 * @return - The new message constructed from the data.
	 * @throws IllegalArgumentException - If the data was incorrectly formed.
	 */
	public static Message deconstructTransmission(String transmission) throws IllegalArgumentException {

		Matcher matcher = MESSAGE_PATTERN.matcher(transmission);

		if(!matcher.matches()) {
			throw new IllegalArgumentException("Given transmission does not follow correct message structure! Trans: " + transmission);
		}

		String receiver;
		String sender;
		try {
			receiver = new String(decoder.decode(matcher.group(1)));
			sender = new String(decoder.decode(matcher.group(2)));
		}
		catch(IllegalArgumentException e) {
			logger.warning("Received transmission did was not correctly encoded! Trans: " + transmission);
			throw new IllegalArgumentException("Transmission sender/receiver was not correctly Base64 encoded!");
		}

		MessageType type;
		try {
			type = MessageType.valueOf(matcher.group(3));
		}
		catch(IllegalArgumentException e) {
			logger.severe("Given transmission does not contain a legal messagetype!");
			throw new IllegalArgumentException("Failed to retrieve a correct messagetype from the given transmission");
		}

		String action = matcher.group(4);

		long timestamp;
		try {
			timestamp = Long.parseLong(matcher.group(5));
		}
		catch(NumberFormatException e) {
			// If this block is executed there is something wrong with our regex!
			logger.severe("Regex failed on timestamp cast!! Please check regex-pattern to make sure it is correct!");
			throw new IllegalArgumentException("Failed to retrieve the timestamp from the given transmission!");
		}

		// Retrieve the arguments
		String[] arg_arr = null;
		String args = matcher.group(6);
		if(!args.equals("")) {
			arg_arr = args.split(";");

			// Decode the arguments
			for(int i = 0; i < arg_arr.length; i++) {
				try {
					arg_arr[i] = URLDecoder.decode( new String(decoder.decode(arg_arr[i])), "UTF-8");
				}
				catch(IllegalArgumentException e) {
					logger.warning("Argument was not correctly base64 encoded! arg: " + arg_arr[i]);
					throw new IllegalArgumentException("Transmission argument was not correctly base64 encoded!");
				}catch(UnsupportedEncodingException e){
					logger.warning("Apparently the encoding UTF-8 does not exist :/ arg: "); 
					throw new IllegalArgumentException("Transmission argument was not correctly URL encoded!");

				
				}
			}
		}

		return new Message(receiver, sender, type, action, timestamp, arg_arr) {
		};
	}

	/**
	 * Returns the receiver of this message.
	 *
	 * @return the receiver of the message
	 */
	public String getReceiver() {
		return RECEIVER;
	}

	/**
	 * Returns the sender behind this message.
	 *
	 * @return The sender of the message
	 */
	public String getSender() {
		return SENDER;
	}

	/**
	 * Returns the type of the message.
	 *
	 * @return The sender of the message
	 */
	public MessageType getType() {
		return TYPE;
	}

	/**
	 * Returns the action defined in the message.
	 *
	 * @return The sender of the message
	 */
	public String getAction() {
		return ACTION;
	}

	/**
	 * Returns the arguments for the action.
	 * NOTE: This will return a copy of the arguments. It will not return
	 * a reference to the actual object that contains the arguments.
	 *
	 * @return - A copy of the arguments.
	 */
	public String[] getArguments() {

		if(ARGUMENTS == null) {
			return null;
		}

		String[] new_array = new String[ARGUMENTS.length];

		// Just to make sure the receiver can't modify the original array as a precaution.
		System.arraycopy(ARGUMENTS, 0, new_array, 0, ARGUMENTS.length);

		return new_array;
	}

	/**
	 * Returns the message's timestamp
	 *
	 * @return The timestamp of the message
	 */
	public long getTimeStamp() {
		return TIME_STAMP;
	}

	/**
	 * Converts the message to a string.
	 *
	 * @return - The message formed as a string.
	 */
	public String getMessage() {
		StringBuilder sb = new StringBuilder();

		sb.append(encoder.encodeToString(RECEIVER.getBytes())).append(';');
		sb.append(encoder.encodeToString(SENDER.getBytes())).append(';');
		sb.append(TYPE).append(';');
		sb.append(ACTION).append(';');
		sb.append(TIME_STAMP).append(';');

		if(ARGUMENTS != null) {
			for(String s : ARGUMENTS) {
				if(s == null) {
					s = "null";
				}
				try{
					sb.append(encoder.encodeToString((URLEncoder.encode(s,"UTF-8")).getBytes())).append(';');
				}catch(UnsupportedEncodingException e){
				
				}
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
