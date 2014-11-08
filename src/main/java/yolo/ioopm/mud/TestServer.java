package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.TestServerAdapter;

public class TestServer extends Server {

	public TestServer() {
		super(0, new TestServerAdapter());
		// TODO Auto-generated constructor stub
	}
	
	public void addMessage(Message msg){
		((TestServerAdapter)adapter).addMessage(msg);
	}
	
	public Message readMessage(){
		return ((TestServerAdapter)adapter).readMessage();
	}

}
