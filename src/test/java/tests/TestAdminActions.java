package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.MessageType;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.game.GameEngine;
import ioopm.mud.game.Keywords;
import ioopm.mud.generalobjects.*;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;

public class TestAdminActions {

	World world;
	DummyAdapter adapter = new DummyAdapter();
	GameEngine ge;
	private static final String player1 = "player1";
	private static final String player1_password = "qwe";
	private static final String player2 = "player2";
	private static final String player2_password = "123";
	private static final String test_room_lobby = "lol room";
	private static final String test_room_unlocked = "the otter kingdom";
	private static final String test_room_locked = "ball room";
	private static final String test_item = "test_item";
	
	
	@Before
	public void setUp() throws Exception {
		makeMeAWorld();
		ge = new GameEngine(adapter, world);
		
		

		} private void dumpMessages(){ for(Message m : adapter.messages){
			System.out.println(m.toHumanForm());
		}
	}


	@Test
	public void testBan()throws BuilderException, EntityNotPresent {

		makeMeAWorld();
		ge = new GameEngine(adapter, world);
			
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));


		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));

		assertFalse("Player 1 was banned at login",world.findPlayer(player1).isBanned());
		assertFalse("Player 2 was banned at login",world.findPlayer(player2).isBanned());
		adapter.flush();

		long time_stamp = System.currentTimeMillis();
		ge.handleMessage(new TestMessage(player1,MessageType.ADMIN_ACTION,"make_admin",time_stamp, makeAdminRequestMessage(time_stamp)));

		assertTrue("Player1 still not admin after supposed sucsessfull login.",world.findPlayer(player1).isAdmin());
		adapter.flush();

		String[] args = {player2};
		ge.handleMessage(new TestMessage(player1,MessageType.ADMIN_ACTION,"ban",time_stamp,args));
		assertTrue("Player 2 was not banned",world.findPlayer(player2).isBanned());
		assertFalse("Player 2 is still logged in",world.findPlayer(player2).isLoggedIn());
		adapter.flush();

		ge.handleMessage(new TestMessage(player2, MessageType.AUTHENTICATION, null, player2,player2_password));

		assertFalse("Player 2 managed to login even though being banned :/",world.findPlayer(player2).isLoggedIn());
		adapter.flush();
	
	
		ge.handleMessage(new TestMessage(player1,MessageType.ADMIN_ACTION,"un_ban",time_stamp,args));
		assertFalse("Player 2 was not un banned",world.findPlayer(player2).isBanned());
		adapter.flush();
	
		ge.handleMessage(new TestMessage(player2, MessageType.AUTHENTICATION, null, player2,player2_password));

		assertTrue("Player 2 failed to login after being banned :/",world.findPlayer(player2).isLoggedIn());
		adapter.flush();
	}


	@Test
	public void testSetAdmin() throws BuilderException, EntityNotPresent {
		makeMeAWorld();
		ge = new GameEngine(adapter, world);
			
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));


		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));
		
		assertFalse("Player 1 was admin at login",world.findPlayer(player1).isAdmin());
		assertFalse("Player 2 was admin at login",world.findPlayer(player2).isAdmin());
		adapter.flush();

		long time_stamp = System.currentTimeMillis();
		ge.handleMessage(new TestMessage(player1,MessageType.ADMIN_ACTION,"make_admin",time_stamp, makeAdminRequestMessage(time_stamp)));

		assertTrue("Player1 still not admin after supposed sucsessfull login.",world.findPlayer(player1).isAdmin());
		assertFalse("Somehow player2 allso became admin :/.",world.findPlayer(player2).isAdmin());
		adapter.flush();

		String[] args = {"balls"};
		ge.handleMessage(new TestMessage(player2,MessageType.ADMIN_ACTION,"make_admin",System.currentTimeMillis(), args));
		assertFalse("Player2 became admin even though he supplied the wrong password.",world.findPlayer(player2).isAdmin());
	}
	
	private static String[]  makeAdminRequestMessage(long time_stamp){

		
		String local_hash = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader("adminpass"));
			StringBuilder sb = new StringBuilder();
			local_hash = br.readLine();

			br.close();

		}catch (FileNotFoundException e){
			fail("No adminpassword file found on the server!");
		}catch (IOException e){
			fail("IOException while trying to read adminpass file!");
		} 

		String local_hash_salted = local_hash + time_stamp;
		byte[] digest = null;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(local_hash_salted.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			digest = md.digest();
		}catch(NoSuchAlgorithmException e){
			fail("No such alogrithm exception occured when hasing stuff!");
		}catch (UnsupportedEncodingException e){
			fail("UnsupportedEncodingException occured!");
		}

		String mathching_hash = String.format("%064x", new java.math.BigInteger(1, digest));
		System.out.println("Hash is: " + mathching_hash);
		String[] args = {mathching_hash};
	
		return args;



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

		protected TestMessage(String sender, MessageType type, String action, long time_stamp, String[] arguments) {
			super("server", sender, type, action, time_stamp, arguments); 
		
		}

		protected TestMessage(String sender, MessageType type,
				String action, String... arguments) {
			super("server", sender, type, action, arguments);
		}

	}

	@SuppressWarnings("serial")
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
	
	class DummyAdapter implements Adapter {
		public DummyAdapter() {
			
		}
		public ArrayList<Message> messages = new ArrayList<>();

		@Override
		public Message poll() {
			return null;
		}

		@Override
		public void sendMessage(Message message) {
			messages.add(message);
		}
		
		public void flush(){
			messages = new ArrayList<>();
		}
	}
	
}
