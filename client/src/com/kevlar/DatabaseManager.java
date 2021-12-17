package com.kevlar;

import java.sql.*;

public class DatabaseManager {
    private Connection sqlConnect() {
        // SQLite connection string
        String url = "jdbc:sqlite:userData.db";
        Connection sqlConnector = null;
        try {
            sqlConnector = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sqlConnector;
    }

    public void createDatabase() {
        Connection connection = this.sqlConnect();
        try {
            // db parameters
            String url = "jdbc:sqlite:userData.db";
            // create a connection to the database
            connection = DriverManager.getConnection(url);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void createTable() {
        String url = "jdbc:sqlite:userData.db";

        // SQL statement for creating a new table
        String createSQL = "CREATE TABLE IF NOT EXISTS kevlarData (\n"
                + "	Title text PRIMARY KEY,\n"
                + "	UserName text NOT NULL,\n"
                + "	Description text NOT NULL,\n"
                + "	Password text NOT NULL \n"
                + ");";

        try (Connection databaseConnection = DriverManager.getConnection(url);
             Statement status = databaseConnection.createStatement()) {
            // create a new table
            status.execute(createSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void insertData(String Title, String userName, String description, String password) {
        String sql = "INSERT INTO kevlarData(Title,userName,description,password) VALUES(?,?)";
        try {
            Connection connection = this.sqlConnect();
            PreparedStatement satement = connection.prepareStatement(sql);
            satement.setString(1, Title);
            satement.setString(2, userName);
            satement.setString(3, description);
            satement.setString(4, password);
            satement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        DatabaseManager inserter = new DatabaseManager();
        // insert three  rows
        //inserter.insertData("test1", "test2", "test3", "test4");

    }


}
