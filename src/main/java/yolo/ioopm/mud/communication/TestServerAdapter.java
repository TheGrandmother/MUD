package yolo.ioopm.mud.communication;

public class TestServerAdapter extends Adapter {
	public void addMessage(Message message){
		inbox.add(message);
	}
	public Message readMessage(){
		return outbox.poll();
	}
}
