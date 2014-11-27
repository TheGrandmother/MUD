package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ServerAdapter;
import yolo.ioopm.mud.game.GameEngine;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	public final static int DEFAULT_PORT = 1337;

	private Adapter    adapter = null;
	private World      world   = new World();
	private GameEngine engine;

	public Server(String path) {

		logger.fine("Attempting to create server adapter...");
		try {
			adapter = new ServerAdapter(DEFAULT_PORT);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, "Server failed to create ServerAdapter on port: " + DEFAULT_PORT, e);
			logger.severe("Severe error! Terminating server...");
			return;
		}

		WorldBuilder wb = new WorldBuilder(path+"items.txt", path+"rooms.txt");
		try {
			wb.buildWorld(world);
		} catch (BuilderException e1) {
			e1.printStackTrace();
		}

		engine = new GameEngine(adapter, world);

		logger.info("Starting main server loop.");
		while(true) {
			Message msg;

			while((msg = adapter.poll()) == null) {
				try {
					Thread.sleep(10);
				}
				catch(InterruptedException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			logger.fine("Received message from client. Calling execute action.");
			engine.handleMessage(msg);
		}
	}
	
	protected Adapter getAdapter(){
		return adapter;
	}
}
