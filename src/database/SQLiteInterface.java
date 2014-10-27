package database;

import generalobjects.Room;

import java.sql.*;

public class SQLiteInterface {

    private final String FOLDER;
    private Connection connection = null;

    public SQLiteInterface(String foldername) {
        this.FOLDER = foldername;
        getConnection();
    }

    private void getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.FOLDER + "/database.db");

            Statement stmt = connection.createStatement();

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS worlds (" +
                    "room_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "room_name VARCHAR," +
                    "north_room_id INTEGER," +
                    "east_room_id INTEGER," +
                    "south_room_id INTEGER," +
                    "west_room_id INTEGER," +
                    "north_door_unlocked BOOLEAN," +
                    "east_door_unlocked BOOLEAN," +
                    "south_door_unlocked BOOLEAN," +
                    "west_door_unlocked BOOLEAN" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS courses (" +
                    "course_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "course_name VARCHAR," +
                    "course_book INTEGER," +
                    "hp INTEGER" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS books (" +
                    "book_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "book_name VARCHAR," +
                    "author VARCHAR," +
                    "print_year INTEGER," +
                    "volume INTEGER" +
                ")"
            );

            stmt.close();
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasConnection() {
        try {
            return connection != null && !connection.isClosed();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void ensureConnection() {
        if(!hasConnection()) {
            getConnection();
        }
    }

    private synchronized boolean updateRoom(Room r) {
        ensureConnection();

        return false;
    }
}
