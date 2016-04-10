package ioopm.mud;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.websocket.WSServerAdapter;
import ioopm.mud.database.PersistentStorage;
import ioopm.mud.database.SQLite;
import ioopm.mud.game.GameEngine;
import ioopm.mud.generalobjects.World;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	public final static int DEFAULT_PORT = 1337;
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	private Adapter adapter = null;
	private World world = new World();
	private GameEngine engine;
	private PersistentStorage storage;

	/**
	 * Constructs a MUD server.
	 * Initiates the server adapter, loads resources, and constructs the world.
	 * Then it starts listening for clients and messages.
	 */
	public Server() throws UnknownHostException {

		/*
		 Server adapter
		 */
		logger.fine("Attempting to create server adapter...");

		WSServerAdapter serverAdapter = new WSServerAdapter(1337);
		serverAdapter.start();
		adapter = serverAdapter;

		/*
		 Load resources
		 */
		logger.fine("Attempting to load resources...");
		try {
			loadResourceFile("items.txt");
			loadResourceFile("rooms.txt");
		} catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}

		/*
		 Initiate database
		 */
		logger.fine("Initiating database...");
		SQLite database;
		try {
			database = new SQLite(new File("storage.db"));
			database.setupDatabase();
		} catch(ClassNotFoundException e) {
			logger.severe("Could not load the JDBC driver!");
			return;
		} catch(SQLException e) {
			logger.severe("Could not establish a connection to the storage file! Message: " + e.getMessage());
			return;
		} catch(IOException e) {
			logger.severe("Something went wrong when checking/creating the database file!");
			return;
		}
		storage = database;

		/*
		 World builder
		 */
		logger.fine("Initiating world builder...");
		WorldBuilder wb = new WorldBuilder("items.txt", "rooms.txt");
		try {
			wb.buildWorld(world);
		} catch(BuilderException e1) {
			e1.printStackTrace();
		}

		engine = new GameEngine(adapter, storage, world);

		/*
		 Main polling loop
		 */
		logger.info("Starting main server loop.");
		while(true) {
			Message msg;

			while((msg = adapter.poll()) == null) {
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			logger.fine("Received message from client. Calling execute action.");
			engine.handleMessage(msg);
		}
	}

	/**
	 * Returns the adapter associated with this server.
	 *
	 * @return - The adapter instance.
	 */
	protected Adapter getAdapter() {
		return adapter;
	}

	/**
	 * Returns the a reference to the persistant storage used by
	 * the server.
	 *
	 * @return - The storage.
	 */
	public PersistentStorage getStorage() {
		return storage;
	}

	/**
	 * Attempts to copy the resource file with given name from  the jar file.
	 * If the file already exists in current directory, the copy will not
	 * be performed.
	 *
	 * @param filename - Name of file to copy.
	 * @throws IOException - If an I/O error occurred
	 * @return - The loaded file.
	 */
	public static File loadResourceFile(String filename) throws IOException {
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
		return items;
	}
}
