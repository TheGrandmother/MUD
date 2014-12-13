package tests.communication;

import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;
import ioopm.mud.communication.messages.client.HandshakeMessage;
import ioopm.mud.communication.server.ClientConnection;
import ioopm.mud.communication.server.runnables.ServerConnectionVerifier;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestServerRunnables {

	/**
	 * Mocks a client connection and the underlying socket.
	 *
	 * @param data          Data to return when a call to cc.readLine() has been made.
	 * @param client_output Where to write data when a call to cc.write() has been made.
	 * @return A mocked ClientConnection object.
	 * @throws IOException Is never actually thrown.
	 */
	private ClientConnection mockClientConnection(String data, OutputStream client_output) throws IOException {
		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("THIS WILL NEVER BE USED!\n".getBytes()));
		when(socket.getOutputStream()).thenReturn(client_output);

		ClientConnection cc = spy(new ClientConnection(socket));
		doReturn(data).when(cc).readLine();
		doReturn("127.0.0.1").when(cc).getIPAddress();

		return cc;
	}

	@Test(timeout = 500)
	public void testConnectionVerifierWithCorrectData() throws IOException {
		ByteArrayOutputStream client_output = new ByteArrayOutputStream();

		ClientConnection cc = mockClientConnection(new HandshakeMessage("foo").getMessage(), client_output);
		Map<String, ClientConnection> connections = new ConcurrentHashMap<>();
		Map<String, Long> timestamps = new ConcurrentHashMap<>();

		ServerConnectionVerifier scv = new ServerConnectionVerifier(cc, connections, timestamps);

		Logger scv_logger = Logger.getLogger(ServerConnectionVerifier.class.getName());
		scv_logger.setLevel(Level.OFF);

		// This can cause a timeout on the test to trigger if the runnable does not work as intended and
		// terminate when finished.
		scv.run();

		// Test if it properly added the connection to the maps
		assertTrue(connections.containsKey("foo"));
		assertTrue(timestamps.containsKey("foo"));

		// Test if the client has been properly notified of the connection
		Message msg = Message.deconstructTransmission(client_output.toString());

		assertEquals("server", msg.getSender());
		assertEquals("unknown", msg.getReceiver());
		assertEquals(MessageType.HANDSHAKE_REPLY, msg.getType());
		assertEquals("true", msg.getArguments()[0]);
	}

	@Test(timeout = 500)
	public void testConnectionVerifierWithIncorrectData() throws IOException {
		ByteArrayOutputStream client_output = new ByteArrayOutputStream();

		ClientConnection cc = mockClientConnection("foo", client_output);
		Map<String, ClientConnection> connections = new ConcurrentHashMap<>();
		Map<String, Long> timestamps = new ConcurrentHashMap<>();

		ServerConnectionVerifier scv = new ServerConnectionVerifier(cc, connections, timestamps);

		Logger scv_logger = Logger.getLogger(ServerConnectionVerifier.class.getName());
		scv_logger.setLevel(Level.OFF);

		// This can cause a timeout on the test to trigger if the runnable does not work as intended and
		// terminate when finished.
		scv.run();

		assertTrue(connections.size() == 0);
		assertTrue(timestamps.size() == 0);
		assertTrue(client_output.size() == 0);
	}
}
