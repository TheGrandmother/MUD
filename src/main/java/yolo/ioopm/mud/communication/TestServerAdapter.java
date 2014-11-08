package yolo.ioopm.mud.communication;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TestServerAdapter extends Adapter {
	public void addMessage(Message message){
		inbox.add(message);
	}
	public Message readMessage(){
		return outbox.poll();
	}
}
