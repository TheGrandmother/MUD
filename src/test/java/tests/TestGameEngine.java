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

public class TestGameEngine {

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
		ge = new GameEngine(adapter, null, world);
		
		

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


	@Test
	public void testEquipAndUnequip() throws BuilderException, EntityNotPresent, Inventory.InventoryOverflow{
		makeMeAWorld();
		ge = new GameEngine(adapter, null, world);
		boolean p1;
		boolean p2;
		
		
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));
		assertTrue("Player 1 not logedin",world.findPlayer(player1).isLoggedIn());
		assertTrue("Player 2 not logedin",world.findPlayer(player2).isLoggedIn());
		adapter.flush();
		
		
		//TEST EQUIP WITH NO ARGS
		
		adapter.flush();
		//world.findPlayer(player1).getInventory().addItem(world.findItem(test_item));
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.EQUIP));
		
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		//assertNull("Item was not removed from inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		//assertTrue("Player did not recive item",world.findPlayer(player1).getWeapon().getName().equals(test_item));
		
		
		
		//TEST EQUIP PROPER
		
		adapter.flush();
		world.findPlayer(player1).getInventory().addItem(world.findItem(test_item));
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.EQUIP, test_item));
		
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.EQUIP_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		assertNull("Item was not removed from inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		assertTrue("Player did not recive item",world.findPlayer(player1).getWeapon().getName().equals(test_item));
		
		
		//TEST EQUIP WHILST SOMETHING IS EQUIPPED
		
		adapter.flush();
		world.findPlayer(player1).getInventory().addItem(world.findItem(test_item));
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.EQUIP, test_item));
		
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		assertNotNull("Item was removed from inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		assertTrue("Player did not recive item",world.findPlayer(player1).getWeapon().getName().equals(test_item));
		
		//TEST UUNEQUIP  WITH OVERFLOW
		
		adapter.flush();
		//world.findPlayer(player1).getInventory().addItem(world.findItem(test_item));

		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.UNEQUIP,test_item));

		p1 = false;
		p2 = false;
		//dumpMessages();
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}

		assertNotNull("Player player still unequiped",world.findPlayer(player1).getWeapon());
		//assertNotNull("Item was not returned to inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		
		//TEST UNEQUIP PROPPER
		
		adapter.flush();
		world.findPlayer(player1).getInventory().removeItem(world.findItem(test_item).getName());
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.UNEQUIP,test_item));
		p1 = false;
		p2 = false;
		//dumpMessages();
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.UNEQUIP_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}

		assertNull("Player did not unequip",world.findPlayer(player1).getWeapon());
		assertNotNull("Item was not returned to inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		
		//TEST EQUIP NON WEAPON
		adapter.flush();
		world.findPlayer(player1).getInventory().removeItem(world.findItem(test_item).getName());
		world.findPlayer(player1).getInventory().addItem(world.findItem(test_item));
		assertNull("Item was not removed from inventory", world.findPlayer(player1).getInventory().findItem("Key to ball room"));
		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.EQUIP, "Key to ball room"));
		
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() ==  MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		//assertNull("Item was not removed from inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		assertNull("Player equiped the key",world.findPlayer(player1).getWeapon());
		
		//TEST EQUIP NON existing item.
		adapter.flush();

		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.EQUIP, "adsf"));
		
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() ==  MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		//assertNull("Item was not removed from inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		assertNull("Player mounted the dumb thing",world.findPlayer(player1).getWeapon());
		
		//TEST UNEQUIP WITH NOTHING EQUIPPED
		adapter.flush();
		world.findPlayer(player1).getInventory().removeItem(world.findItem(test_item).getName());
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.UNEQUIP,test_item));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}

		assertNull("Player did not unequip",world.findPlayer(player1).getWeapon());
		//assertNotNull("Item was not returned to inventory", world.findPlayer(player1).getInventory().findItem(test_item));
		
		//TEST EQUIP NON WEAPON
		adapter.flush();
		world.findPlayer(player1).getInventory().removeItem(world.findItem(test_item).getName());
		world.findPlayer(player1).getInventory().addItem(world.findItem("Key to ball room"));
		
		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.EQUIP, "Key to ball room"));
		
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() ==  MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		assertNotNull("Item was removed from inventory", world.findPlayer(player1).getInventory().findItem("Key to ball room"));
		assertNull("Player equiped the key",world.findPlayer(player1).getWeapon());
		
		
		
		
		
	}	
	
	@Test
	public void testTakeAndDrop() throws BuilderException, EntityNotPresent{
		makeMeAWorld();
		ge = new GameEngine(adapter, null, world);
		
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		adapter.flush();
		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));
		adapter.flush();
		assertTrue("Player 1 not logedin",world.findPlayer(player1).isLoggedIn());
		assertTrue("Player 2 not logedin",world.findPlayer(player2).isLoggedIn());
		
		int start_amount = 0;
		for(ItemContainer ic: world.findRoom(test_room_lobby).getItems()){
			if(ic.getName().equals(test_item)){
				start_amount = ic.getAmount();
			}
		}
		
		//TEST TAKE PROPPER
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.TAKE, test_item));
		boolean p1 = false;
		boolean p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.TAKE_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
				p2 = true;
			}
		}
		
		if(!p1 || !p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		int current_amount = 0;
		for(ItemContainer ic: world.findRoom(test_room_lobby).getItems()){
			if(ic.getName().equals(test_item)){
				current_amount = ic.getAmount();
			}
		}
		assertTrue("Amount did not decrease by one",current_amount == start_amount - 1);
		assertFalse("Player did not recive item",world.findPlayer(player1).getInventory().findItem(test_item)==null); 
		
		//TEST nonexsiting item.
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.TAKE, "123"));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		assertNull("Lol... picked up notexsisting item",world.findPlayer(player1).getInventory().findItem("123"));
		
		//Test item in other room
		adapter.flush();

		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.TAKE, "Key to ball room"));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}

		assertNull("Lol... picked up item from other room",world.findPlayer(player1).getInventory().findItem("Key to ball room")); 
		
		//Test wrong args
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.TAKE));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		//TEST inventory full
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.TAKE, test_item));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		current_amount = 0;
		for(ItemContainer ic: world.findRoom(test_room_lobby).getItems()){
			if(ic.getName().equals(test_item)){
				current_amount = ic.getAmount();
			}
		}
		
		assertTrue("Amount in room is "+current_amount,current_amount == start_amount-1);
		
		int lol_amount = 0;
		for(ItemContainer ic: world.findPlayer(player1).getInventory().getitems()){
			if(ic.getName().equals(test_item)){
				lol_amount = ic.getAmount();
			}
		}
		assertTrue("Lol... player got more the item :P", lol_amount == 1);
		
		
		//----DROP-----
		//TEST DROP PROPER
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.DROP, test_item));
		p1 = false;
		p2 = false;
		
		try{
			for (Message msg : adapter.messages) {
				if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.DROP_REPLY)){
					p1 = true;
				}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
					p2 = true;
				}
			}
		}catch (NullPointerException e ){
			dumpMessages();
			fail("got NPE");
		}
		if(!p1 || !p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		current_amount = 0;
		for(ItemContainer ic: world.findRoom(test_room_lobby).getItems()){
			if(ic.getName().equals(test_item)){
				current_amount = ic.getAmount();
			}
		}
		assertTrue("Amount did not increase by one",current_amount == start_amount);
		assertNull("Player did not drop item",world.findPlayer(player1).getInventory().findItem(test_item));
		
		//Test drop non existing
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.DROP, "123"));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		assertNull("Somehow managed to have the nonexisting item",world.findPlayer(player1).getInventory().findItem("123"));
		
		//TEST dropping unpossesed item
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.DROP, "axe"));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		} 
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		assertNull("Somehow managed to have the nonexisting item",world.findPlayer(player1).getInventory().findItem("123"));
		
		
		//TEST wrong args
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.DROP));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		assertNull("Somehow managed to have the nonexisting item",world.findPlayer(player1).getInventory().findItem("123"));
		
		
		
		
	}
	
	@Test
	public void testMove() throws BuilderException, EntityNotPresent, Inventory.InventoryOverflow{
		makeMeAWorld();
		ge = new GameEngine(adapter, null, world);
		adapter.flush();
		
		//Login players. This is tested in another function
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		adapter.flush();
		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));
		adapter.flush();
		assertTrue("Player 1 not logedin",world.findPlayer(player1).isLoggedIn());
		assertTrue("Player 2 not logedin",world.findPlayer(player2).isLoggedIn());
		
		//TEST PROPPER
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, test_room_unlocked));
		boolean p1 = false;
		boolean p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.MOVE_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
				p2 = true;
			}
		}
		
		if(!p1 || !p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		assertTrue("Player1 not aassociated with " + test_room_unlocked,world.findPlayer(player1).getLocation().getName().equals(test_room_unlocked));
		
		//MOVE BACK
		adapter.flush();
		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, test_room_lobby));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.MOVE_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
				p2 = true;
			}
		}
		
		if(!p1 || !p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		assertTrue("Player1 not aassociated with " + test_room_unlocked,world.findPlayer(player1).getLocation().getName().equals(test_room_lobby));
		
		//TEST MOVE TO LOCKED WITHOUT KEY
		adapter.flush();
		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, test_room_locked));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertFalse("Player1 moved to locked room without key",world.findPlayer(player1).getLocation().getName().equals(test_room_locked));
		
		
		//TEST MOVE TO LOCKED WITH KEY BUT UNSATISFACTORY LEVEL
		adapter.flush();
		world.findPlayer(player1).getCs().setLevel(0);
		world.findPlayer(player1).getInventory().addItem(world.findItem("Key to "+test_room_locked));
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, test_room_locked));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType() == MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertFalse("Player1 moved to locked room without key",world.findPlayer(player1).getLocation().getName().equals(test_room_locked));
		world.findPlayer(player1).getCs().setLevel(1);
		
		
		//TEST MOVE TO LOCKED WITH KEY
		adapter.flush();
		//world.findPlayer(player1).getInventory().addItem(world.findItem("Key to "+test_room_locked));
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, test_room_locked));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.MOVE_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
				p2 = true;
			}
		}
		
		if(!p1 || !p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertTrue("Player1 didn't move to locked room with key",world.findPlayer(player1).getLocation().getName().equals(test_room_locked));
		
		//TEST MOVE TO NON ADJACENT ROOM
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, test_room_unlocked));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType()== MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertTrue("Player1 moved to silly room",world.findPlayer(player1).getLocation().getName().equals(test_room_locked));
		
		
		//TEST MOVE TO NONEXISTING ROOM
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, "ndsfjghdjfjgjksdfnjkgsö"));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType()== MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getType() == MessageType.NOTIFICATION){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertTrue("Player1 moved to silly room",world.findPlayer(player1).getLocation().getName().equals(test_room_locked));
		
		//Test wrong arguments
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType()== MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertTrue("Player1 moved to silly room",world.findPlayer(player1).getLocation().getName().equals(test_room_locked));
		
		//Test Moving to current room
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.MOVE, world.findPlayer(player1).getLocation().getName()));
		p1 = false;
		p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType()== MessageType.GENERAL_ERROR){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		
		if(!p1 || p2){
			dumpMessages();
			fail("Say:Players did not recive propperreplys.");
		}
		
		assertTrue("Player1 moved to silly room",world.findPlayer(player1).getLocation().getName().equals(world.findPlayer(player1).getLocation().getName()));
		
	
	}
	 
	
	@Test
	public void testTalk() throws BuilderException, EntityNotPresent{
		makeMeAWorld();
		ge = new GameEngine(adapter, null, world);
		
		//Login players. This is tested in another function
		ge.handleMessage(new TestMessage(player1, MessageType.REGISTRATION, null, player1,player1_password));
		adapter.flush();
		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, player2,player2_password));
		adapter.flush();
		assertTrue("Player 1 not logedin",world.findPlayer(player1).isLoggedIn());
		assertTrue("Player 2 not logedin",world.findPlayer(player2).isLoggedIn());

		
		//Test SAY
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.SAY, "lol"));
		boolean p1 = false;
		boolean p2 = false;
		
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.SAY_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getAction().equals(Keywords.SAY_REPLY)){
				p2 = true;
			}
		}
		
		if(!p1 || !p2){
			dumpMessages();
			fail("Say:Players did not recive replys.");
		}
		
		//Test Say with worng args
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.SAY));
		p1 = false;
		p2 = false;
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType().equals(MessageType.GENERAL_ERROR)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		assertTrue("Player 1 did not recive error:",p1);
		assertFalse("Player 2 recieved message:",p2);
		
		//Test Whisper 
		adapter.flush();
		
		//Test propper:
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.WHISPER, player2,"test"));
		p1 = false;
		p2 = false;
		assertFalse("No messages sent",adapter.messages.isEmpty());
	
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getAction().equals(Keywords.WHISPER_REPLY)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2) && msg.getAction().equals(Keywords.WHISPER_REPLY)){
				p2 = true;
			}
		}
		
		if(!p1 || !p2){
			dumpMessages();
			fail("Whisper:Players did not recive replys.");
		}
		
		//Test wrong name
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.WHISPER, "153","test"));
		p1 = false;
		p2 = false;
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType().equals(MessageType.GENERAL_ERROR)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		assertTrue("Player 1 did not recive error:",p1);
		assertFalse("Player 2 recieved message:",p2);
		
		//Test self name
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.WHISPER, player1,"test"));
		p1 = false;
		p2 = false;
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType().equals(MessageType.GENERAL_ERROR)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		assertTrue("Player 1 did not recive error:",p1);
		assertFalse("Player 2 recieved message:",p2);
		
		//test player in other room
		adapter.flush(); 
		world.findPlayer(player2).getLocation().removePlayer(world.findPlayer(player2));
		world.findPlayer(player2).setLocation(world.findRoom("ball room"));
		assertTrue("Player could not be added",world.findRoom("ball room").addPlayer(world.findPlayer(player2)));
		
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.WHISPER, player2,"test"));
		p1 = false;
		p2 = false;
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType().equals(MessageType.GENERAL_ERROR)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		assertTrue("Player 1 did not recive error:",p1);
		assertFalse("Player 2 recieved message:",p2);
		
		//Test whisper with wrong args
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.GENERAL_ACTION, Keywords.WHISPER));
		p1 = false;
		p2 = false;
		for (Message msg : adapter.messages) {
			if(msg.getReceiver().equals(player1) && msg.getType().equals(MessageType.GENERAL_ERROR)){
				p1 = true;
			}else if(msg.getReceiver().equals(player2)){
				p2 = true;
			}
		}
		assertTrue("Player 1 did not recive error:",p1);
		assertFalse("Player 2 recieved message:",p2);
	
	}
	
	@Test
	public void testAuthentication() throws BuilderException{
		makeMeAWorld();
		ge = new GameEngine(adapter, null, world);
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

		adapter.flush();
		

		//test username validity
		String fail_name = "balls <i>";
		ge.handleMessage(new TestMessage(player2, MessageType.REGISTRATION, null, fail_name,"asd"));
		m = getMessageOfType(MessageType.REGISTRATION_REPLY);
		assertTrue("Registration possible with mallformed username.",m.getArguments()[0].equals("false"));
		try {
			world.findPlayer(fail_name).isLoggedIn();
			fail("Player still appeared in the world");
		} catch (EntityNotPresent e) {
			
		}


		//Test Logging out
		adapter.flush();
		ge.handleMessage(new TestMessage(player1, MessageType.LOGOUT, null, player1));
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
