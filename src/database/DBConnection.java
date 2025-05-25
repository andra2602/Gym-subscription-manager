package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:GYM.db"; // The path to the database file
    private static DBConnection instance; // singleton
    private Connection connection; // The actual connection to the database

    // Private constructor - only this class can create an instance of itself
    private DBConnection() {
        try {
            connection = DriverManager.getConnection(URL); // The connection to the database is created using DriverManager (standard JDBC).
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }


    // Static method to access the single instance of the class
    // If the instance is null, create a new one.
    // If already exists, return the same instance.
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection(); // if it doesn't exist, it creates it
        }
        return instance;
    }

    // Public method to access the connection
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL); // Reîncarcă conexiunea dacă e închisă
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
