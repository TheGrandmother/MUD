package ioopm.mud.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite implements PersistentStorage {

	final static String DATABASE_STRUCTURE =
		// Tables
		"CREATE TABLE IF NOT EXISTS player ("+
			"id INTEGER PRIMARY KEY, " +
			"username TEXT NOT NULL, " +
			"password_hash TEXT NOT NULL, " +
			"hash_salt TEXT NOT NULL" +
		");" +
		"CREATE TABLE IF NOT EXISTS inventory (" +
			"player_id INTEGER, " +
			"volume INTEGER NOT NULL, " +
			"max_volume INTEGER NOT NULL, " +
			"FOREIGN KEY(player_id) REFERENCES player(id)" +
		");" +
		"CREATE TABLE IF NOT EXISTS item (" +
			"item_id INTEGER PRIMARY KEY, " +
			"level INTEGER NOT NULL, " +
			"description TEXT NOT NULL, " +
			"size INTEGER NOT NULL, " +
			"dropable BOOLEAN NOT NULL" +
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

	private final Connection database_connection;

	public SQLite(File database_file) throws ClassNotFoundException, SQLException, IOException {

		// Load JDBC driver into the JVM
		Class.forName("org.sqlite.JDBC");

		// Make sure there is a file to work against
		database_file.createNewFile();

		// Establish a connection to the local databasefile
		database_connection = DriverManager.getConnection("jdbc:sqlite:" + database_file.getAbsolutePath());
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
	}
}
