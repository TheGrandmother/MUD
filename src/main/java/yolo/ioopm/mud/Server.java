package yolo.ioopm.mud;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.server.ServerAdapter;
import yolo.ioopm.mud.game.GameEngine;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;

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

		//TODO Create a better system for this
		BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("enter path to world files!");
		String path = null;
		
		try {
			path = buff.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		WorldBuilder wb = new WorldBuilder(path+"items.txt", path+"rooms.txt");
		wb.buildWorld(world);

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
			engine.executeAction(msg);
		}
	}
	
	protected Adapter getAdapter(){
		return adapter;
	}
}
