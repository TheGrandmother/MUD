package ioopm.mud.database;

import ioopm.mud.generalobjects.CharacterSheet;
import ioopm.mud.generalobjects.Inventory;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLite implements PersistentStorage {

	private static final String DATABASE_STRUCTURE =
		// Tables
		"CREATE TABLE IF NOT EXISTS player (" +
			"id INTEGER PRIMARY KEY, " +
			"username TEXT UNIQUE NOT NULL, " +
			"location INTEGER REFERENCES room(id), " +
			"password TEXT NOT NULL, " +
			"salt TEXT, " + //TODO Set this field to not null when enabling hashing and salting
			"is_admin BOOLEAN NOT NULL" +
		");" +

		"CREATE TABLE IF NOT EXISTS inventory (" +
			"player_id INTEGER, " +
			"volume INTEGER NOT NULL, " +
			"max_volume INTEGER NOT NULL, " +
			"FOREIGN KEY(player_id) REFERENCES player(id)" +
		");" +

		"CREATE TABLE IF NOT EXISTS weapon (" +
			"item_id INTEGER REFERENCES item(id), " +
			"name TEXT UNIQUE NOT NULL, " +
			"description TEXT NOT NULL, " +
			"size INTEGER NOT NULL, " +
			"level INTEGER NOT NULL, " +
			"damage INTEGER NOT NULL" +
		");" +

		"CREATE TABLE IF NOT EXISTS key (" +
			"item_id INTEGER REFERENCES item(id), " +
			"target INTEGER REFERENCES room(id)" +
		");" +

		"CREATE TABLE IF NOT EXISTS character_sheet (" +
			"player_id INTEGER UNIQUE NOT NULL, " +
			"hp INTEGER, " +
			"health INTEGER, " +
			"max_health INTEGER, " +
			"level INTEGER, " +
			"FOREIGN KEY(player_id) REFERENCES player(id)" +
		");" +

		"CREATE TABLE IF NOT EXISTS room (" +
			"id INTEGER PRIMARY KEY, " +
			"name TEXT UNIQUE NOT NULL, " +
			"description TEXT, " +
			"pvp BOOLEAN NOT NULL" +
		");" +

		// M-N Relations
		"CREATE TABLE IF NOT EXISTS inventory_item_relation (" +
			"inv_id INTEGER, " +
			"item_id INTEGER, " +
			"amount INTEGER, " +
			"FOREIGN KEY(inv_id) REFERENCES inventory(player_id), " +
			"FOREIGN KEY(item_id) REFERENCES item(id), " +
			"PRIMARY KEY(inv_id, item_id)" +
		");" +

		"CREATE TABLE IF NOT EXISTS room_item_relation (" +
			"room_id INTEGER REFERENCES room(id), " +
			"item_id INTEGER REFERENCES item(id), " +
			"amount INTEGER NOT NULL" +
		");" +

		"CREATE TABLE IF NOT EXISTS room_exit_relation (" +
			"room_id INTEGER REFERENCES room(id), " +
			"exit_id INTEGER REFERENCES room(id)" +
		");";

	private static final String INSERT_PLAYER =
		// This can be shorten to look like INSERT_CS
		"INSERT OR REPLACE INTO player(id, username, location, password, is_admin) " +
		"SELECT old.id, new.username, new.location, new.password, new.is_admin " +
		"FROM ( SELECT " +
			"? AS username, " +
			"? AS location, " +
			"? AS password, " +
			"? AS is_admin " +
		") AS new " +
		"LEFT JOIN ( " +
			"SELECT id, username " +
			"FROM player " +
		") AS old ON new.username = old.username;";

	private static final String INSERT_CS =
		"INSERT OR REPLACE INTO character_sheet(player_id, hp, health, max_health, level) " +
		"VALUES ((" +
			"SELECT id FROM player where player.username = ? " +
		"), ?, ?, ?, ?) ";

	private static final String INSERT_ITEM =
		"INSERT ";

	private static final Logger logger = Logger.getLogger(SQLite.class.getName());

	private final Connection database_connection;

	public SQLite(File database_file) throws ClassNotFoundException, SQLException, IOException {

		// Load JDBC driver into the JVM
		Class.forName("org.sqlite.JDBC");

		// Make sure there is a file to work against
		database_file.createNewFile();

		// Setup database configuration
		SQLiteConfig conf = new SQLiteConfig();
		conf.enforceForeignKeys(true);

		// Establish a connection to the local databasefile
		database_connection = DriverManager.getConnection("jdbc:sqlite:" + database_file.getAbsolutePath(), conf.toProperties());

		logger.info("Database initiated.");
	}

	/**
	 * Constructs all tables and relations in the database.
	 * Does not overwrite already defined tables/relations.
	 */
	public void setupDatabase() throws SQLException {
		logger.fine("Constructing database...");

		Statement statement = database_connection.createStatement();

		String[] statements = DATABASE_STRUCTURE.split(";");
		for(String s : statements) {
			statement.addBatch(s);
		}

		statement.executeBatch();
		statement.close();

		logger.fine("Database has been constructed!");
	}

	@Override
	public void storePlayer(Player player) throws IllegalArgumentException {
		try {
			PreparedStatement stmt = database_connection.prepareStatement(INSERT_PLAYER);

			// Ugly as fuck reflection solution to password encapsulation.
			String password;
			try {
				Field pass_field = Player.class.getDeclaredField("password");
				pass_field.setAccessible(true);
				password = (String) pass_field.get(player);
			}
			catch(NoSuchFieldException | IllegalAccessException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				throw new IllegalArgumentException("Could not access password field in player! Check logs for stack trace.");
			}

			stmt.setString(1, player.getName());
			stmt.setString(2, player.getLocation().getName());
			stmt.setString(3, password);
			stmt.setBoolean(4, player.isAdmin());

			stmt.execute();
			stmt.close();
		}
		catch(SQLException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return;
		}

		storeCharacterSheet(player.getName(), player.getCs());
		storeInventory(player.getInventory());
	}

	private void storeCharacterSheet(String username, CharacterSheet cs) {
		try {
			PreparedStatement stmt = database_connection.prepareStatement(INSERT_CS);

			stmt.setString(1, username);
			stmt.setInt(2, cs.getHp());
			stmt.setInt(3, cs.getHealth());
			stmt.setInt(4, cs.getMaxHealth());
			stmt.setInt(5, cs.getLevel());

			stmt.execute();
			stmt.close();
		}
		catch(SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void storeInventory(Inventory inventory) {

	}

	@Override
	public Player loadPlayer(String username, World world) throws IllegalArgumentException {
			
		return null;
	}
}
