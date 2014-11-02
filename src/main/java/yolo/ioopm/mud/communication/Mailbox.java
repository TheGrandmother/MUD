package yolo.ioopm.mud.communication;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Mailbox<E> extends ConcurrentLinkedQueue {

	/**
	 * Calls poll on the underlaying ConcurrentLinkedQueue object until it's empty
	 *
	 * @return All objects in it, never null, might be empty
	 */
	public ArrayList<E> pollAll() {
		ArrayList<E> messages = new ArrayList<>();

		E msg;
		while((msg = (E) poll()) != null) {
			messages.add(msg);
		}

		return messages;
	}
}
