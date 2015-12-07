package tests.communication;

import ioopm.mud.communication.rawtcp.Adapter;
import ioopm.mud.communication.rawtcp.Message;
import ioopm.mud.communication.rawtcp.MessageType;
import ioopm.mud.communication.rawtcp.client.runnables.ClientMessageListener;
import ioopm.mud.communication.rawtcp.client.runnables.ClientMessageSender;
import ioopm.mud.communication.rawtcp.messages.client.LogoutMessage;
import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class TestClientRunnables {

	@Test(timeout = Adapter.TICKRATEMILLIS * 2)
	public void testMessageSender() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Queue<Message> outbox = new ConcurrentLinkedQueue<>();

		StringBuffer sb;
		synchronized(pw) {
			ClientMessageSender sender = new ClientMessageSender(pw, outbox);

			Thread t = new Thread(sender);
			t.start();

			// Hand the message to the sender.
			outbox.offer(new LogoutMessage("foo"));

			// Wait for the sender to notify that it has sent the message.
			try {
				pw.wait();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}

			// Stop the runnable
			sender.stop();

			// Retrieve the message sent.
			sb = sw.getBuffer();
		}

		// Deconstruct the data
		Message msg = Message.deconstructTransmission(sb.toString());

		// Test some random stuff to make sure it went through alright.
		assertEquals("server", msg.getReceiver());
		assertEquals("foo", msg.getSender());
		assertEquals("null", msg.getAction());
		assertEquals(MessageType.LOGOUT, msg.getType());
	}

	@Test(timeout = Adapter.TICKRATEMILLIS * 2)
	public void testMessageListener() {
		String sent = new LogoutMessage("foo").getMessage();

		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sent.getBytes())));
		Queue<Message> inbox = new LinkedList<>();

		ClientMessageListener cml = new ClientMessageListener(br ,inbox);

		// Disable the CML's logger.
		Logger l = Logger.getLogger(ClientMessageListener.class.getName());
		l.setLevel(Level.OFF);

		// Start the CML in a separate thread.
		Thread t = new Thread(cml);
		t.start();

		// Poll inbox till we get the message.
		Message msg;
		while((msg = inbox.poll()) == null); // BLOCKING!!

		// Stop the listener
		cml.stop();

		String received = msg.getMessage();

		assertEquals(sent, received);
	}
}
