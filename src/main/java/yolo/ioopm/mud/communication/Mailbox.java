package yolo.ioopm.mud.communication;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Mailbox<E> extends ConcurrentLinkedQueue {

	public ArrayList<E> popAll() {
		ArrayList<E> messages = new ArrayList<>();

		E msg;
		while((msg = (E) poll()) != null) {
			messages.add(msg);
		}

		return messages;
	}
}
