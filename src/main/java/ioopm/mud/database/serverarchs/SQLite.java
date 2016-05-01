package ioopm.mud.database.serverarchs;

import ioopm.mud.database.SQLCode;
import ioopm.mud.database.PersistentStorage;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;
import org.sqlite.SQLiteConfig;

import java.io.*;
import java.sql.*;
import java.util.logging.Logger;

public class SQLite implements PersistentStorage {

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

		for(SQLCode s : SQLCode.values()) {
			statement.addBatch(s.toString());
		}

		statement.executeBatch();
		statement.close();

		logger.fine("Database has been constructed!");
	}

	@Override
	public void storePlayer(Player player) throws IllegalArgumentException {
	}

	@Override
	public Player loadPlayer(String username, World world) throws IllegalArgumentException {
		return null;
	}
}
