package yolo.ioopm.mud.communication;


import yolo.ioopm.mud.communication.messages.IncomingMessage;

/**
 * This is the class which specifies the messages. These messages are sent to the Adapter and from there
 * translated and sent to the server.
 * <p/>
 * Se specs/message-protocol.txt for a description of the message protocol.
 *
 * @author TheGrandmother
 */

public abstract class Message {

	private final String   RECEIVER;
	private final String   SENDER;
	private final String   ACTION;
	private final String[] ARGUMENTS;
	private final long     TIME_STAMP;

	/**
	 * This will be a constructor which creates a message to be sent to the Adapter
	 *
	 * @param reciever
	 * @param sender
	 * @param action
	 * @param nouns
	 */
	public Message(String reciever, String sender, String action, String... nouns) {
		this(reciever, sender, action, System.currentTimeMillis(), nouns);
	}

	public Message(String reciever, String sender, String action, long time_stamp, String... nouns) {
		this.RECEIVER = reciever;
		this.SENDER = sender;
		this.ACTION = action;
		this.ARGUMENTS = nouns;
		this.TIME_STAMP = time_stamp;
	}

	public String getReceiver() {
		return RECEIVER;
	}

	public String getSender() {
		return SENDER;
	}

	public String getAction() {
		return ACTION.toString();
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
		sb.append(ACTION).append(';');
		sb.append(TIME_STAMP).append(';');

		if(ARGUMENTS != null) {
			for(String s : ARGUMENTS) {
				sb.append(s).append(';');
			}
		}

		return sb.toString();
	}

	/**
	 * This function takes a string received by the adapter and converts it to a Message object;
	 *
	 * @param transmission
	 * @return
	 */
	public static IncomingMessage deconstructTransmission(String transmission) {

		String[] sa = transmission.split(";");

		// Length of smallest message, currently the heartbeat
		if(sa.length < 4) {
			return null;
		}

		int delta = sa.length - 4;
		String[] nouns = null;

		if(delta > 0) {
			nouns = new String[delta];
			try {
				System.arraycopy(sa, 4, nouns, 0, delta);
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("System.arraycopy failed! ArrayIndexOutOfBounds! delta:" + delta);
				nouns = null;
			}
		}

		return new IncomingMessage(sa[0], sa[1], sa[2], Long.valueOf(sa[3]), nouns);
	}
}
