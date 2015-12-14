package ioopm.mud.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {

	// TABLES
	final static String TABLE_PLAYER = "CREATE TABLE IF NOT EXISTS player ("+
			"id INTEGER PRIMARY KEY, " +
			"username TEXT NOT NULL, " +
			"password_hash TEXT NOT NULL, " +
			"hash_salt TEXT NOT NULL" +
		")";

	final static String TABLE_INVENTORY = "CREATE TABLE IF NOT EXISTS inventory (" +
			"player_id INTEGER, " +
			"volume INTEGER NOT NULL, " +
			"max_volume INTEGER NOT NULL, " +
			"FOREIGN KEY(player_id) REFERENCES player(id)" +
		")";


	final static String TABLE_ITEM = "CREATE TABLE IF NOT EXISTS item (" +
			"item_id INTEGER PRIMARY KEY, " +
			"level INTEGER NOT NULL, " +
			"description TEXT NOT NULL, " +
			"size INTEGER NOT NULL, " +
			"dropable BOOLEAN NOT NULL" +
		")";

	final static String TABLE_CHARACTERSHEET = "CREATE TABLE IF NOT EXISTS character_sheet (" +
			"player_id INTEGER, " +
		")";

	// M-N Relations
	final static String INVENTORY_ITEM_RELATION = "CREATE TABLE IF NOT EXISTS inventory_item_relation (" +
			"inv_id INTEGER, " +
			"item_id INTEGER, " +
			"amount INTEGER, " +
			"FOREIGN KEY(inv_id) REFERENCES inventory(player_id), " +
			"FOREIGN KEY(item_id) REFERENCES item(item_id)" +
		")";

	private final Connection database_connection;

	public SQLite(File database_file) throws ClassNotFoundException, SQLException {

		// Load JDBC driver into the JVM
		Class.forName("org.sqlite.JDBC");

		// Establish a connection to the local databasefile
		database_connection = DriverManager.getConnection("jdbc:sqlite:" + database_file.getAbsolutePath());
	}

	/**
	 * Constructs all tables and relations in the database.
	 * Does not overwrite already defined tables/relations.
	 */
	public void setupDatabase() {

	}
}
