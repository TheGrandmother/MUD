package tests.communication;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;
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

//		StringBuffer sb;
//		synchronized(sw) {
//			ClientMessageSender sender = new ClientMessageSender(new PrintWriter(sw), outbox);
//
//			Thread t = new Thread(sender);
//			t.start();
//
//			// Hand the message to the sender.
//			outbox.offer(new LogoutMessage("foo"));
//
//			// Wait for the sender to notify that it has sent the message.
//			try {
//				sw.wait();
//			}
//			catch(InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			// Stop the runnable
//			sender.stop();
//
//			// Retrieve the message sent.
//			sb = sw.getBuffer();
//		}

		// Deconstruct the data
		Message msg = Message.deconstructTransmission(sb.toString());

		// Test some random stuff to make sure it went through alright.
		assertEquals("server", msg.getReceiver());
		assertEquals("fo", msg.getSender());
		assertEquals("null", msg.getAction());
		assertEquals(MessageType.LOGOUT, msg.getType());
	}
}
