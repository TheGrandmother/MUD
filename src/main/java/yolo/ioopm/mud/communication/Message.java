package yolo.ioopm.mud.communication;


import java.util.logging.Logger;

/**
 * This is the class which specifies the messages. These messages are sent to the Adapter and from there
 * translated and sent to the server.
 * <p>
 * Each message is on the form<br>
 * <code>\<sender\>;\<recievcer\>;\<action\>;\<time_stamp\>;\<argument1\>;\<argument2\>;.....;\<argumentN\></code>
 *
 * @author TheGrandmother
 */

public abstract class Message {

	private static final Logger logger = Logger.getLogger(Message.class.getName());

	private final String      RECEIVER;
	private final String      SENDER;
	private final MessageType TYPE;
	private final String      ACTION;
	private final String[]    ARGUMENTS;
	private final long        TIME_STAMP;

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
	public static Message deconstructTransmission(String transmission) {

		String[] sa = transmission.split(";");

		// Length of smallest message, currently the heartbeat
		if(sa.length < 5) {
			throw new IllegalArgumentException("The transmission was not correctly formed!");
		}

		int delta = sa.length - 5;
		String[] nouns = null;

		if(delta > 0) {
			nouns = new String[delta];
			try {
				System.arraycopy(sa, 5, nouns, 0, delta);
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("System.arraycopy failed! ArrayIndexOutOfBounds! delta:" + delta);
				nouns = null;
			}
		}

		return new Message(sa[0], sa[1], MessageType.valueOf(sa[2]), sa[3], Long.valueOf(sa[4]), nouns) {
			// "Instantiate" the Message-class by creating an empty sub-class
		};
	}

	/**
	 * Returns the receiver of this message.
	 *
	 * @return the reciver of the message
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

		sb.append(RECEIVER).append(';');
		sb.append(SENDER).append(';');
		sb.append(TYPE).append(';');
		sb.append(ACTION).append(';');
		sb.append(TIME_STAMP).append(';');

		if(ARGUMENTS != null) {
			for(String s : ARGUMENTS) {
				sb.append(s).append(';');
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
