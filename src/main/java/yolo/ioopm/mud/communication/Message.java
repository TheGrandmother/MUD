package yolo.ioopm.mud.communication;


/**
 * This is the class which specifies the messages. These messages are sent to the Adapter and from there
 * translated and sent to the server.
 * <p/>
 * Each message is on the form<br>
 * \<sender\>;\<recievcer\>;\<action\>;\<time_stamp\>;\<argument1\>;\<argument2\>;.....;\<argumentN\>
 *
 * @author TheGrandmother
 */

public abstract class Message {

	private final String      RECEIVER;
	private final String      SENDER;
	private final MessageType TYPE;
	private final String      ACTION;
	private final String[]    ARGUMENTS;
	private final long        TIME_STAMP;

	/**
	 * This will be a constructor which creates a message to be sent to the Adapter
	 *
	 * @param receiver
	 * @param sender
	 * @param action
	 * @param nouns
	 */
	public Message(String receiver, String sender, MessageType type, String action, String... nouns) {
		this(receiver, sender, type, action, System.currentTimeMillis(), nouns);
	}

	public Message(String receiver, String sender, MessageType type, String action, long time_stamp, String... nouns) {
		this.RECEIVER = receiver;
		this.SENDER = sender;
		this.TYPE = type;
		this.ACTION = action;
		this.ARGUMENTS = nouns;
		this.TIME_STAMP = time_stamp;
	}

	/**
	 * This function takes a string received by the adapter and converts it to a Message object;
	 *
	 * @throws java.lang.IllegalArgumentException
	 * @param transmission
	 * @return
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

	public String getReceiver() {
		return RECEIVER;
	}

	public String getSender() {
		return SENDER;
	}

	public MessageType getType() {
		return TYPE;
	}

	public String getAction() {
		return ACTION;
	}

	public String[] getArguments() {
		String[] new_array = new String[ARGUMENTS.length];

		// Just to make sure the receiver can't modify the original array as a precaution.
		System.arraycopy(ARGUMENTS, 0, new_array, 0, ARGUMENTS.length);

		return new_array;
	}

	public long getTimeStamp() {
		return TIME_STAMP;
	}

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
