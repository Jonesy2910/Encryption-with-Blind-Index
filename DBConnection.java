package org.apache.maven.archetypes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection connection;
        try {
            // JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Creating connection to mysql
            System.out.println("Connecting to the database");
            String url = "jdbc:mysql://localhost:3306/encrypted_database";
            String username = "root";
            String password = "IndividualPassword1107";
            connection = DriverManager.getConnection(url, username, password);
        } catch(SQLException e) {
            System.err.println("Cannot Connect to Database");
            throw new IllegalStateException();
        }
        System.out.println("Connected");
        return connection;
    }
}