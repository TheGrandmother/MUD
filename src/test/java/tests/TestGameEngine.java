package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;
import ioopm.mud.communication.messages.server.ActionMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.game.GameEngine;
import ioopm.mud.game.Keywords;
import ioopm.mud.game.RuntimeTests;
import ioopm.mud.generalobjects.World;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.corba.se.spi.ior.MakeImmutable;

import sun.misc.Queue;

public class TestGameEngine {

	World world;
	DummyAdapter adapter = new DummyAdapter();
	GameEngine ge;
	private static final String player1 = "player1";
	private static final String player1_password = "qwe";
	private static final String player2 = "player1";
	private static final String player2_password = "123";
	
	@Before
	public void setUp() throws Exception {
		makeMeAWorld();
		ge = new GameEngine(adapter, world);
		
	}
	
	public void testTalk() throws BuilderException{
		makeMeAWorld();
		ge = new GameEngine(adapter, world);
		
		//Login players This is tested in another function
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));
		adapter.flush();
		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.SAY, "lol"));
		boolean p1 = false;
		boolean p2 = false;
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.SAY_REPLY)){
				p1 = true;
			}
			if(msg.getReceiver().equals(player2) && msg.getAction().equals(Keywords.SAY_REPLY)){
				p2 = true;
			}
		}
		
		
	}
	
	@Test
	public void testAuthentication() throws BuilderException{
		makeMeAWorld();
		ge = new GameEngine(adapter, world);
		adapter.flush();
		Message m;
		
		//test registration
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		m = getMessageOfType(MessageType.REGISTRATION_REPLY);
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("true"));
		try {
			assertTrue("Player still not registered.",world.findPlayer(player1).isLoggedIn());
		} catch (EntityNotPresent e) {
			fail("Player not present in the world.");
		}
		
		//Test Logging out
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.LOGOUT, null, player1));
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("true"));
		try {
			assertFalse("Player din't log out.",world.findPlayer(player1).isLoggedIn());
		} catch (EntityNotPresent e) {
			fail("Player dissapeared.");
		}
		
		//Test login
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.AUTHENTICATION, null, player1,player1_password));
		m = getMessageOfType(MessageType.AUTHENTICATION_REPLY);
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("true"));
		try {
			assertTrue("Player not logedin.",world.findPlayer(player1).isLoggedIn());
		} catch (EntityNotPresent e) {
			fail("Player not present in the world.");
		}
		//failure tests
		//loging in a already loged in player.
		adapter.flush();
		try {
			assertTrue("Player not logedin.",world.findPlayer(player1).isLoggedIn());
		} catch (EntityNotPresent e) {
			fail("Player not present in the world.");
		}
		ge.handleMessage(new TestMessage(player1, MessageType.AUTHENTICATION, null, player1,player1_password));
		m = getMessageOfType(MessageType.AUTHENTICATION_REPLY);
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("false"));
		
		//test registration of a already present name.
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.LOGOUT, null, player1));
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		m = getMessageOfType(MessageType.REGISTRATION_REPLY);
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("false"));
		try {
			assertFalse("Player got loged in.",world.findPlayer(player1).isLoggedIn());
		} catch (EntityNotPresent e) {
			fail("Player not present in the world.");
		}
		//test loging out a loged out player
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.LOGOUT, null, player1));
		m = getMessageOfType(MessageType.SERIOUS_ERROR);
		assertFalse("Dint recieve an error message.", m == null);
		
		//Test logging in with wrong password
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.AUTHENTICATION, null, player1,"dfasdfew"));
		m = getMessageOfType(MessageType.AUTHENTICATION_REPLY);
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("false"));
		//Test logging in with nonexisting username
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.AUTHENTICATION, null, "asd",player1_password));
		m = getMessageOfType(MessageType.AUTHENTICATION_REPLY);
		assertTrue(m.getArguments()[1],m.getArguments()[0].equals("false"));
		
		
		
		
	}
	
	
	public void makeMeAWorld() throws BuilderException{
		
		/*
		 TEST WORLD:
		 
		room:lol room: lamest room ever!.:false: none :ball room;true,the otter kingdom;false:
		room:ball room: giggle....:true: axe;150 :lol room;false,toilet;false:
		room:the otter kingdom:Here the all mighty otter rules over France!:true:Key to ball room;10:lol room;false
		room:toilet:the shitter:true:tp;20:ball room;false:
		lobby:lol room;0,ball room;5
		
		key:room2:0
		key:ball room:0
		weapon:axe:a stupid axe:5:0:20
		weapon:tp:a magnificent roll of toilet paper.:12:2:35
		*/
		world = new World();
		WorldBuilder wb = new WorldBuilder("world files/test_items.txt", "world files/test_rooms.txt");
		wb.buildWorld(world);
	}
	
	private Message getMessageOfType(MessageType t){
		for(Message msg : adapter.messages){
			if(msg.getType().equals(t)){
				return msg;
			}
		}
		return null;
	}
	
	private boolean assertMessageType(MessageType t){
		for(Message msg : adapter.messages){
			if(msg.getType().equals(t)){
				return true;
			}
		}
		return false;
	}
	
	private boolean assertAction(String action){
		for(Message msg : adapter.messages){
			if(msg.getAction().equals(action)){
				return true;
			}
		}
		return false;
	}
	
	class TestMessage extends Message{

		protected TestMessage(String sender, MessageType type,
				String action, String... arguments) {
			super("server", sender, type, action, arguments);
			// TODO Auto-generated constructor stub
		}

	}
	
	private class WrongMessage extends Exception{
		Message msg;
		public WrongMessage(Message msg) {
			super();
			this.msg = msg;
		}
		
		public Message getMsg() {
			return msg;
		}
	}
	
	class DummyAdapter extends Adapter{
		public DummyAdapter() {
			
		}
		public ArrayList<Message> messages = new ArrayList<>();
		@Override
		public void sendMessage(Message message) {
			messages.add(message);
		}
		
		public void flush(){
			messages = new ArrayList<>();
		}
	}
	
}