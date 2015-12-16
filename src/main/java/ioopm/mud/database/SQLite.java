package ioopm.mud.database;

import ioopm.mud.generalobjects.CharacterSheet;
import ioopm.mud.generalobjects.Inventory;
import ioopm.mud.generalobjects.Player;

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
			"username TEXT NOT NULL, " +
			"description TEXT, " + // Is this needed?
			"starting_location TEXT NOT NULL, " + // For proper safety, this should be a foreign key
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

		"CREATE TABLE IF NOT EXISTS item (" +
			"item_id INTEGER PRIMARY KEY, " +
			"name TEXT NOT NULL, " +
			"level INTEGER NOT NULL, " +
			"description TEXT NOT NULL, " +
			"size INTEGER NOT NULL, " +
			"dropable BOOLEAN NOT NULL" + // TODO add generalization for keys (has target) and weapons (has damage)
		");" +

		"CREATE TABLE IF NOT EXISTS character_sheet (" +
			"player_id INTEGER, " +
			"hp INTEGER, " +
			"health INTEGER, " +
			"max_health INTEGER, " +
			"level INTEGER, " +
			"FOREIGN KEY(player_id) REFERENCES player(id)" +
		");" +

		// M-N Relations
		"CREATE TABLE IF NOT EXISTS inventory_item_relation (" +
			"inv_id INTEGER, " +
			"item_id INTEGER, " +
			"amount INTEGER, " +
			"FOREIGN KEY(inv_id) REFERENCES inventory(player_id), " +
			"FOREIGN KEY(item_id) REFERENCES item(item_id)" +
		")";

	private static final String INSERT_PLAYER =
		"INSERT INTO player(username, description, starting_location, password, is_admin) VALUES(?,?,?,?,?);";

	private static final String INSERT_CS =
		"INSERT INTO character_sheet(player_id, hp, health, max_health, level) " +
			"SELECT player.id, ?, ?, ?, ? " +
				"FROM player " +
				"WHERE player.username = ?;";

	private static final Logger logger = Logger.getLogger(SQLite.class.getName());

	private final Connection database_connection;

	public SQLite(File database_file) throws ClassNotFoundException, SQLException, IOException {
		// Load JDBC driver into the JVM
		Class.forName("org.sqlite.JDBC");

		// Make sure there is a file to work against
		database_file.createNewFile();

		// Establish a connection to the local databasefile
		database_connection = DriverManager.getConnection("jdbc:sqlite:" + database_file.getAbsolutePath());

		logger.info("Database initiated.");
	}

	/**
	 * Constructs all tables and relations in the database.
	 * Does not overwrite already defined tables/relations.
	 */
	public void setupDatabase() throws SQLException {
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
				pass_field.setAccessible(false);
			}
			catch(NoSuchFieldException | IllegalAccessException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				throw new IllegalArgumentException("Could not access password field in player! Check logs for stack trace.");
			}

			stmt.setString(1, player.getName());
			stmt.setString(2, player.getDescription());
			stmt.setString(3, player.getLocation().getName());
			stmt.setString(4, password);
			stmt.setBoolean(5, player.isAdmin());

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

			stmt.setInt(1, cs.getHp());
			stmt.setInt(2, cs.getHealth());
			stmt.setInt(3, cs.getMaxHealth());
			stmt.setInt(4, cs.getLevel());
			stmt.setString(5, username);

			stmt.execute();
			stmt.close();
		}
		catch(SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}
	}

	private void storeInventory(Inventory inventory) {

	}

	@Override
	public Player loadPlayer(String username) throws IllegalArgumentException {
		return null;
	}
}
