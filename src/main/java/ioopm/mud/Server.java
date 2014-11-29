package ioopm.mud;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.Message;
import ioopm.mud.communication.server.ServerAdapter;
import ioopm.mud.game.GameEngine;
import ioopm.mud.generalobjects.World;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	public final static int DEFAULT_PORT = 1337;

	private Adapter    adapter = null;
	private World      world   = new World();
	private GameEngine engine;

	public Server() {

		/*
		 Server adapter
		 */
		logger.fine("Attempting to create server adapter...");
		try {
			adapter = new ServerAdapter(DEFAULT_PORT);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, "Server failed to create ServerAdapter on port: " + DEFAULT_PORT, e);
			logger.severe("Severe error! Terminating server...");
			return;
		}

		/*
		 Load resources
		 */
		logger.fine("Attempting to load resources...");
		try {
			loadResourceFile("items.txt");
			loadResourceFile("rooms.txt");
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}

		/*
		 World builder
		 */
		logger.fine("Initiating world builder...");
		WorldBuilder wb = new WorldBuilder("items.txt", "rooms.txt");
		try {
			wb.buildWorld(world);
		} catch (BuilderException e1) {
			e1.printStackTrace();
		}

		engine = new GameEngine(adapter, world);

		/*
		 Main polling loop
		 */
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

	/**
	 * Attempts to copy the resource file with given name from  the jar file.
	 * If the file already exists in current directory, the copy will not
	 * be performed.
	 *
	 * @param filename - Name of file to copy.
	 * @throws IOException - If an I/O error occurred
	 */
	private void loadResourceFile(String filename) throws IOException {
		File items = new File(filename);
		if(!items.exists()) {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			FileOutputStream fos = new FileOutputStream(items);
			int data;
			while((data = is.read()) != -1) {
				fos.write(data);
			}
			fos.flush();
		}
	}
}
