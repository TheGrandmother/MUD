package ioopm.mud.database;

public enum DatabaseStructure {

	TABLE_PLAYER(
		"CREATE TABLE IF NOT EXISTS Player(" +
			"id INTEGER PRIMARY KEY," +
			"username VARCHAR NOT NULL UNIQUE," +
			"password VARCHAR NOT NULL," +
			"inventory_volume INTEGER NOT NULL," +
			"is_admin BOOLEAN DEFAULT FALSE" +
		");"
	),

	TABLE_PLAYERITEMS(
		"CREATE TABLE IF NOT EXISTS PlayerItems(" +
			"player_id INTEGER," +
			"item_id INTEGER," +
			"amount NOT NULL," +

			"PRIMARY KEY(player_id, item_id)," +

			"FOREIGN KEY(player_id) REFERENCES Player(id)" +
				"ON DELETE CASCADE" +
				"ON UPDATE RESTRICT" +
		");"
	),

	TABLE_CHARACTERSHEET(
		"CREATE TABLE IF NOT EXISTS CharacterSheet(" +
			"player_id INTEGER PRIMARY KEY," +
			"health INTEGER," +
			"health_max INTEGER," +
			"level INTEGER," +
			"hp INTEGER," +

			"FOREIGN KEY(player_id) REFERENCES Player(id)" +
				"ON DELETE CASCADE" +
				"ON UPDATE RESTRICT" +
		");"
	),

	TABLE_ITEMSAT(
		"CREATE TABLE IF NOT EXISTS ItemsAt(" +
			"location_id INTEGER," +
			"item_id INTEGER," +
			"amount INTEGER," +

			"PRIMARY KEY(location_id, item_id)" +
		");"
	);

	private final String SQL;

	DatabaseStructure(String sql) {
		SQL = sql;
	}

	public String toString() {
		return SQL;
	}
}