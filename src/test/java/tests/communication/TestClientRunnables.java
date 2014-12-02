package tests.communication;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.client.runnables.ClientMessageSender;
import ioopm.mud.communication.messages.client.LogoutMessage;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class TestClientRunnables {

	@Test
	public void testMessageSender() {
		StringWriter sw = new StringWriter();
		Queue<Message> outbox = new ConcurrentLinkedQueue<>();

		Runnable sender = new ClientMessageSender(new PrintWriter(sw), outbox);

		Thread t = new Thread(sender);
		t.start();

		try {
			sw.wait();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}



		StringBuffer sb = sw.getBuffer();
	}
}
