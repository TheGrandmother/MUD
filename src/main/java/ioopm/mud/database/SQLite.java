package ioopm.mud.database;

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
			"description TEXT, " +
			"starting_location TEXT NOT NULL, " + // For proper safety, this should be a foreign key
			"password TEXT NOT NULL, " +
			"salt TEXT" +
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
		"INSERT INTO player(username, description, starting_location, password) VALUES(?,?,?,?);";

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
	public void storePlayer(Player player) {
		try {
			PreparedStatement stmt = database_connection.prepareStatement(INSERT_PLAYER);

			stmt.setString(1, player.getName());
			stmt.setString(2, player.getDescription());
			stmt.setString(3, player.getLocation().getName());

			String password;
			try {
				Field pass_field = Player.class.getDeclaredField("password");
				pass_field.setAccessible(true);
				password = (String) pass_field.get(player);
				pass_field.setAccessible(false);
			}
			catch(NoSuchFieldException | IllegalAccessException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				return;
			}

			stmt.setString(4, password);

			stmt.execute();
			stmt.close();
		}
		catch(SQLException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public Player loadPlayer(String username) throws IllegalArgumentException {
		return null;
	}
}
