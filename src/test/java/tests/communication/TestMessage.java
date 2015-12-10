package tests.communication;

import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;
import ioopm.mud.communication.messages.client.GeneralActionMessage;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestMessage {

	/**
	 * Uses reflection to overwrite the field "TIME_STAMP" in the abstract message class with the given value.
	 * @param msg Message to overwrite.
	 * @param value Value to overwrite with.
	 */
	private static void overwriteTimeStamp(Message msg, long value) {
		try {
			Field ts = msg.getClass().getSuperclass().getDeclaredField("TIME_STAMP");
			ts.setAccessible(true);
			ts.set(msg, value);
			ts.setAccessible(false);
		}
		catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeconstructTransmission() {
		String data = "server;greger;REGISTRATION;null;1417377860384;greger;123;";

		Message msg = Message.deconstructTransmission(data);
		String[] args = msg.getArguments();

		assertEquals("server", msg.getReceiver());
		assertEquals("greger", msg.getSender());
		assertEquals(MessageType.REGISTRATION, msg.getType());
		assertEquals("null", msg.getAction());
		assertEquals(1417377860384L, msg.getTimeStamp());
		assertEquals("greger", args[0]);
		assertEquals("123", args[1]);
	}

	@Test
	public void testArgumentsEncapsulation() {
		Message msg = new GeneralActionMessage("player", "foo", new String[]{"foo", "bar"});

		String[] args1 = msg.getArguments();
		String[] args2 = msg.getArguments();

		args1[0] = "bar";

		assertNotEquals(args1[0], args2[0]);
	}

	@Test
	public void testGetMessage() {
		Message msg = new GeneralActionMessage("player", "foo", new String[]{"foo", "bar"});

		// In order to test this method we need to overwrite the value stored in TIME_STAMP with our value.
		overwriteTimeStamp(msg, 12L);

		assertEquals("server;player;GENERAL_ACTION;foo;12;foo;bar;", msg.getMessage());
	}

	@Test
	public void testGetMessage2() {
		Message msg = new Message("server", "player", MessageType.GENERAL_ACTION, "foo", null) {
		};

		// In order to test this method we need to overwrite the value stored in TIME_STAMP with our value.
		overwriteTimeStamp(msg, 12L);

		assertEquals("server;player;GENERAL_ACTION;foo;12;", msg.getMessage());
	}

	@Test
	public void testDeconstruct1() {
		String str = "server;foo;LOGOUT;null;1234;";

		Message msg = Message.deconstructTransmission(str);

		assertEquals("server", msg.getReceiver());
		assertEquals("foo", msg.getSender());
		assertEquals(MessageType.LOGOUT, msg.getType());
		assertEquals(1234L, msg.getTimeStamp());

		assertEquals(str, msg.toString());
	}

	@Test
	public void testToString() {
		Message msg = new GeneralActionMessage("player", "foo", new String[]{"foo", "bar"});
		assertEquals(msg.toString(), msg.getMessage());
	}
}
