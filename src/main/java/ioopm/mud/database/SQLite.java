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

	private static final String DATABASE_STRUCTURE  = "";

	private static final String INSERT_PLAYER = "";
	private static final String INSERT_CS = "";


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
		//try {
		//	PreparedStatement stmt = database_connection.prepareStatement(INSERT_PLAYER);

		//	// Ugly as fuck reflection solution to password encapsulation.
		//	String password;
		//	try {
		//		Field pass_field = Player.class.getDeclaredField("password");
		//		pass_field.setAccessible(true);
		//		password = (String) pass_field.get(player);
		//	}
		//	catch(NoSuchFieldException | IllegalAccessException e) {
		//		logger.log(Level.SEVERE, e.getMessage(), e);
		//		throw new IllegalArgumentException("Could not access password field in player! Check logs for stack trace.");
		//	}

		//	stmt.setString(1, player.getName());
		//	stmt.setString(2, player.getLocation().getName());
		//	stmt.setString(3, password);
		//	stmt.setBoolean(4, player.isAdmin());

		//	stmt.execute();
		//	stmt.close();
		//}
		//catch(SQLException e) {
		//	logger.log(Level.WARNING, e.getMessage(), e);
		//	return;
		//}

		//storeCharacterSheet(player.getName(), player.getCs());
		//storeInventory(player.getInventory());
	}

	private void storeCharacterSheet(String username, CharacterSheet cs) {
	//	try {
	//		PreparedStatement stmt = database_connection.prepareStatement(INSERT_CS);

	//		stmt.setString(1, username);
	//		stmt.setInt(2, cs.getHp());
	//		stmt.setInt(3, cs.getHealth());
	//		stmt.setInt(4, cs.getMaxHealth());
	//		stmt.setInt(5, cs.getLevel());

	//		stmt.execute();
	//		stmt.close();
	//	}
	//	catch(SQLException e) {
	//		logger.log(Level.SEVERE, e.getMessage(), e);
	//	}
	}

	private void storeInventory(Inventory inventory) {

	}

	@Override
	public Player loadPlayer(String username, World world) throws IllegalArgumentException {
			
		return null;
	}
}
