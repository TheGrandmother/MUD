package ioopm.mud.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {

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
