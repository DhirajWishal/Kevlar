package com.kevlar;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.io.File;  // Import the File class
import java.util.Base64;
import java.util.Scanner;

// Specify the filename

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

    }

    public void getTitleDescription() {
        String sqlQuery = "SELECT Title,description FROM kevlarData";
        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sqlQuery)) {


            while (results.next()) {
                System.out.println(results.getString("Title") + "\t" +
                        results.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public String getPassword(String userTitle) {
        String password = null;
        String sqlQuery = "SELECT password FROM kevlarData" +
                "WHERE Title=" + userTitle;
        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sqlQuery)) {

            if (results.next()) {
                password = results.getString("password");

            } else {
                System.out.println("Title is not found");
                password = null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (password);
    }

    public String getUserName(String userTitle) {
        String sqlQuery = "SELECT userName FROM kevlarData" +
                "WHERE Title=" + userTitle;
        String userName = null;
        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sqlQuery)) {

            if (results.next()) {
                userName = results.getString("userName");
            } else {
                System.out.println("Title is not found");
                userName = null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (userName);
    }


    public static String getHmac(String validation) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        File databsaeFile = new File("userData.db");
        Scanner myReader = new Scanner(databsaeFile);
        byte[] content = Files.readAllBytes(Paths.get("userData.db"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(validation.getBytes(), "SHA-256");
        Mac mac = Mac.getInstance("SHA-256");
        mac.init(secretKeySpec);
        byte[] byteHmac=mac.doFinal(content);
        String finalHMACKey= Base64.getEncoder().encodeToString(byteHmac);
        return finalHMACKey;
    }

    public static String base64TheFile() throws IOException {
        File dataBsaeFile = new File("userData.db");
        byte[]  databaseFileBytes = Files.readAllBytes(Paths.get(String.valueOf(dataBsaeFile)));
        String base64File=Base64.getEncoder().encodeToString(databaseFileBytes);
        return base64File;

    }


}
