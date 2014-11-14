package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.TestServerAdapter;

public class TestServer extends Server {

	TestServerAdapter testAdapter;

	public TestServer() {
		super(new TestServerAdapter());
	}
	
	public void addMessage(Message msg){
		//testAdapter.addMessage(msg);
		((TestServerAdapter)getAdapter()).addMessage(msg);
	}
	
	public Message readMessage(){
		//return testAdapter.readMessage();
		return ((TestServerAdapter)getAdapter()).readMessage();
		
	}

}
