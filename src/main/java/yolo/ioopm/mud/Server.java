package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ServerAdapter;
import yolo.ioopm.mud.game.GameEngine;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.items.Key;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	public final static int DEFAULT_PORT = 1337;

	private Adapter    adapter = null;
	private World      world   = new World();
	private GameEngine engine;

	public Server() {
		logger.fine("Attempting to create server adapter...");
		try {
			adapter = new ServerAdapter(DEFAULT_PORT);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, "Server failed to create ServerAdapter on port: " + DEFAULT_PORT, e);
			logger.severe("Severe error! Terminating server...");
			return;
		}

		engine = new GameEngine(adapter, world);

		logger.fine("Adding test data to server!");
		addTestData();

		logger.info("Starting main server loop.");
		while(true) {
			Message msg;

			while((msg = adapter.poll()) == null) {
				try {
					Thread.sleep(100);
				}
				catch(InterruptedException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			logger.fine("Received message from client. Calling execute action.");
			engine.executeAction(msg);
		}
	}
	
	protected Adapter getAdapter(){
		return adapter;
	}

	// This is only a temporary method for testing during development!
	// TODO remove this
	private void addTestData() {
		try {
			world.addRoom(new Room("room1", "of doom"));
			world.addRoom(new Room("room2", "super silly"));
			world.addItem(new Key("room1", "room2", 0));
			world.findRoom("room1").addItem(world.findItem("Key to room2"));
			world.findRoom("room1").addExit(world.findRoom("room2"), true);
			world.findRoom("room2").addExit(world.findRoom("room1"), false);
			world.addCharacter(new Pc("player1", "has a hat", "123", world.findRoom("room1")));
			world.addCharacter(new Pc("player2", "aint hasing a hat", "123", world.findRoom("room1")));
			world.addCharacter(new Pc("player3", "aint hasing a hat", "123", world.findRoom("room1")));
		}
		catch(World.EntityNotUnique e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(World.EntityNotPresent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
