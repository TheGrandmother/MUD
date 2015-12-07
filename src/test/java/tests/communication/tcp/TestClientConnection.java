package tests.communication.tcp;

import ioopm.mud.communication.rawtcp.server.ClientConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestClientConnection {

	private Socket           socket;
	private ClientConnection cc;

	private ByteArrayInputStream  input  = new ByteArrayInputStream("foo\n".getBytes());
	private ByteArrayOutputStream output = new ByteArrayOutputStream();

	@Before
	public void setUp() throws IOException {
		// Mocking socket
		socket = mock(Socket.class);

		// Mocked SocketAddress
		SocketAddress sa = new SocketAddress() {
			@Override
			public String toString() {
				return "localhost";
			}
		};

		// Stubbing socket
		when(socket.getOutputStream()).thenReturn(output);
		when(socket.getInputStream()).thenReturn(input);
		when(socket.getRemoteSocketAddress()).thenReturn(sa);

		// Create object to test
		cc = new ClientConnection(socket);
	}

	@Test
	public void hasUnreadData() throws IOException {
		// NOTE! This test has to be run before testReadLine!
		// This is because that test will empty the InputStream.
		assertTrue(cc.hasUnreadData());
	}

	@Test
	public void testReadLine() throws IOException {
		String received = cc.readLine();

		// Check the message was read correctly.
		assertEquals("foo", received);

		// Make sure the InputStream now is empty.
		assertTrue(!cc.hasUnreadData());
	}

	@Test
	public void testWrite() throws UnsupportedEncodingException {
		output.reset();
		cc.write("foo");
		String received = output.toString("UTF-8");
		assertEquals("foo\n", received);
	}

	@Test
	public void testGetIPAddress() {
		assertEquals("localhost", cc.getIPAddress());
	}
}
