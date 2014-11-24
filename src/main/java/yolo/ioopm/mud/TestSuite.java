package yolo.ioopm.mud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.TestServerAdapter;
import yolo.ioopm.mud.communication.messages.server.IncommingMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.game.GameEngine;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.World.EntityNotUnique;
import yolo.ioopm.mud.generalobjects.items.Key;
import yolo.ioopm.mud.generalobjects.items.Weapon;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;

public class TestSuite {

	World             world   = new World();
	TestServerAdapter adapter = new TestServerAdapter();
	GameEngine        game    = new GameEngine(adapter, world);
	WorldBuilder wb = new WorldBuilder("world files/items.txt", "world files/rooms.txt");

	public Boolean quit = false;

	public TestSuite() {
		
	}

	public static void main(String[] args) {
		System.out.println("Starting test suite");
		TestSuite t = new TestSuite();

		try {
			t.wb.buildWorld(t.world);
			t.world.addRoom(new Room("room1", "of doom",true));
			t.world.addRoom(new Room("room2", "super silly"));
			t.world.addRoom(new Room("room3", "super silly",true));
			//t.world.addItem(new Key( "room2", 0));
			t.world.addItem(new Weapon("sword", "lool", 3, 1,5));
		
			t.world.findRoom("room1").addItem(t.world.findItem("Key to room2"));
			t.world.findRoom("room1").addItem(t.world.findItem("axe"));
			t.world.findRoom("room1").addItem(t.world.findItem("sword"));
			t.world.findRoom("room1").addItem(t.world.findItem("sword"));
			t.world.findRoom("room1").addItem(t.world.findItem("sword"));
			t.world.findRoom("room1").addItem(t.world.findItem("sword"));
			t.world.findRoom("room1").addExit(t.world.findRoom("room2"), true);
			t.world.findRoom("room1").addExit(t.world.findRoom("lol room"), false);
			t.world.findRoom("room2").addExit(t.world.findRoom("room1"), false);
			t.world.findRoom("room1").addExit(t.world.findRoom("room3"), false);
			t.world.findRoom("room2").addExit(t.world.findRoom("room3"), false);
			t.world.findRoom("room3").addExit(t.world.findRoom("room1"), false);
		}
		catch(EntityNotUnique e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(EntityNotPresent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			
			t.world.addCharacter(new Pc("player1", "has a hat", "123", t.world.findRoom("room1")));
			t.world.addCharacter(new Pc("player2", "aint hasing a hat", "123", t.world.findRoom("room1")));
			t.world.addCharacter(new Pc("player3", "aint hasing a hat", "123", t.world.findRoom("room1")));
			t.world.findPc("player1").setLoggedIn(true);
			t.world.findPc("player2").setLoggedIn(true);
			t.world.findPc("player3").setLoggedIn(true);
		} catch (EntityNotUnique e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityNotPresent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			System.out.println(t.world.findPc("player1"));
		} catch (EntityNotPresent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Thread read_outgoing =  new Thread(t.new Listener(t.adapter), "listen");
		Thread send =  new Thread(t.new Writer(t.adapter), "sänd");
		
		read_outgoing.start();
		send.start();
		
		while(true){
			Message msg = t.adapter.poll();
			if(msg != null){
				t.game.executeAction(msg);
			}
		}
		
		//t.quit = true;
		//System.out.println("Ended da thest suite");
		//return;
	}

	class Listener implements Runnable{
		TestServerAdapter adapter;
		public Listener(TestServerAdapter a) {
			this.adapter = a;
			}
		
		Message msg;
		@Override
		public void run() {
			while(!quit){
				
				msg = adapter.readMessage();
				if(msg != null){
					System.out.print(msg.getSender() +"->"+msg.getReceiver()+": ");
					System.out.print(msg.getAction()+"\n");
					for (int i = 0; i < msg.getArguments().length; i++) {
						System.out.print(msg.getArguments()[i]+"\n");
					}
					System.out.println("");
				}
			}
			
		}
		
	}
	
	class Writer implements Runnable{
		TestServerAdapter adapter;
		public Writer(TestServerAdapter a) {
			this.adapter = a;
			}
		BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));

		String actor;
		String action;
		String[] nouns;
		@Override
		public void run() {
			while(!quit){
				try {
					System.out.println("Who are you:");
					actor = buff.readLine();
					if(actor.equals("q")){quit=true;}
					System.out.println("What do you want to do:");
					action = buff.readLine();
					if(action.equals("q")){quit=true;}
					System.out.println("Tajp arguments sepparated by one sweet colon");
					nouns = buff.readLine().split(",");
					adapter.addMessage(new IncommingMessage("server", actor, action, nouns));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	
	
}






