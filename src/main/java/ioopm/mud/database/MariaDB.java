package ioopm.mud.database;

import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MariaDB implements PersistentStorage {

	private static final Logger logger = Logger.getLogger(MariaDB.class.getName());

	private final Connection database_connection;

	public MariaDB(String host, int port, String database, String username, String password) throws SQLException {
		database_connection = DriverManager.getConnection(
			"jdbc:mariadb://" + host + ":" + port + "/" + database, username, password
		);

		logger.info("Established connection with MariaDB/MySQL-database!");

		setupDatabase();
	}

	/**
	 * Constructs all tables and relations in the database.
	 * Does not overwrite already defined tables/relations.
	 */
	public void setupDatabase() throws SQLException {
		logger.fine("Constructing database...");

		Statement statement = database_connection.createStatement();

		for(DatabaseStructure s : DatabaseStructure.values()) {
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
