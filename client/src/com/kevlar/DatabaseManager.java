package com.kevlar;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;


public class DatabaseManager {
    /**
     * This function creates a connection between the SQL database and the client
     */
    private Connection sqlConnect() {
        // Connects to the "userData.bb" File
        String url = "jdbc:sqlite:userData.db";
        Connection sqlConnector = null;
        try {
            sqlConnector = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sqlConnector;
    }

    /**
     * This function creates the SQL table within the SQL database using SQL statements
     */
    public void createTable() {
        // SQL statement for creating a new table with the name kevlarData
        String createSQL = """
                CREATE TABLE IF NOT EXISTS kevlarData (
                	Title text PRIMARY KEY,
                	UserName text NOT NULL,
                	Description text NOT NULL,
                	Password text NOT NULL\s
                );""";

        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement()) {
            // create a new table
            statement.execute(createSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This function INSERTS data into the SQL table using the input from the user as parameters
     *
     * @param Title       User's Title (The primary key of the table eg:- Google , Email , Discord etc..)
     * @param userName    User's Username to each of the corresponding Titles
     * @param description A small description containing a hint of the password
     * @param password    User's Password for the certain Title
     */
    public void insertData(String Title, String userName, String description, String password) {
        //The SQL statement
        String sql = "INSERT INTO kevlarData(Title,userName,description,password) VALUES(?,?,?,?)";
        try {
            Connection connection = this.sqlConnect();
            PreparedStatement statement = connection.prepareStatement(sql);
            //Adding the data in the order of how the SQL database was created
            statement.setString(1, Title);
            statement.setString(2, userName);
            statement.setString(3, description);
            statement.setString(4, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(Title + " password already exists!!");
        }

        DatabaseManager inserter = new DatabaseManager();
    }

    /**
     * A custom function to extract the all the titles and descriptions  from the SQL
     */
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

    /**
     * Get all the titles in the database.
     *
     * @return The title list.
     */
    public ArrayList<String> queryTitles() {
        String sqlQuery = "SELECT Title FROM kevlarData";
        ArrayList<String> titleList = new ArrayList<>();
        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sqlQuery)) {

            while (results.next())
                titleList.add(results.getString("Title"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return titleList;
    }

    /**
     * This function will get the user's Title and return the specific password
     *
     * @param userTitle The user's desired Title to get the password from
     * @return The corresponding Password
     */
    public String getPassword(String userTitle) {
        String password = null;
        //The SQL statement
        String sqlQuery = "SELECT password FROM kevlarData " +
                "WHERE Title=\"" + userTitle + "\"";
        //Connects to the SQL
        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sqlQuery)) {
            //Condition to output the specifc password
            if (results.next()) {
                password = results.getString("password");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (password);
    }

    /**
     * This function is to get the username from the title and output the username
     *
     * @param userTitle User's input for the Title
     * @return The username
     */
    public String getUserName(String userTitle) {
        //SQL statment
        String sqlQuery = "SELECT userName FROM kevlarData " +
                "WHERE Title=\"" + userTitle + "\"";
        String userName = null;
        try (Connection connection = this.sqlConnect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sqlQuery)) {

            if (results.next()) {
                userName = results.getString("userName");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (userName);
    }

    /**
     * This fucntion takes the "userData.db" File and turns the whole file into a H mac format
     *
     * @param validation User's Validation key
     * @return The base64 encoded File
     * @throws IOException              Makes sure no error is thrown when .readAllBytes is used
     * @throws NoSuchAlgorithmException Makes sure when the data is requested and its available
     * @throws InvalidKeyException      makes sure the key is correct and not in a wrong length
     */
    public static String getHmac(String validation) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        byte[] content = Files.readAllBytes(Paths.get("userData.db"));
        byte[] encodedFile = Base64.getEncoder().encode(content);
        byte[] validationBase64 = validation.getBytes();
        //Generates a Key using the validation Key
        SecretKeySpec secretKeySpec = new SecretKeySpec(validationBase64, "SHA-256");
        //Using SHA-256
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        //byteHmac is given the final value of the H Mac
        byte[] byteHmac = mac.doFinal(encodedFile);
        //Encodes the bytes into the String and returns it
        return Base64.getEncoder().encodeToString(byteHmac);
    }

    /**
     * This function creates a new file and then Base64 encodes the whole file and returns it
     *
     * @return The file converted to binary data
     * @throws IOException
     */
    public static String base64TheFile() throws IOException {
        File dataBaseFile = new File("userData.db");
        byte[] databaseFileBytes = Files.readAllBytes(Paths.get(String.valueOf(dataBaseFile)));
        return Base64.getEncoder().encodeToString(databaseFileBytes);
    }

    /**
     * Checks if the user's password is matching with the database stored in the database
     *
     * @param title    The Title used to check for which one they need the password for
     * @param password Password entered by the user
     * @return Boolean validity , True if the password is there in the database, False if password is not found
     */
    public Boolean checkForPassword(String title, String password) {
        String sqlQuery = "SELECT password FROM kevlarData " +
                "WHERE Title=\"" + title + "\"";
        boolean validity = false;
        try (Connection connection = this.sqlConnect()) {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sqlQuery);
            while (results.next()) {
                String databasePassword = results.getString("password");
                //compares the Database password and the password entered by the user
                if (databasePassword.equals(password)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return validity;
    }

    /**
     * This function changes the password stored in the database to another one
     *
     * @param title       The title the password belongs to
     * @param newPassword The new password replacing the old password
     */
    public void changePassword(String title, String newPassword) {
        String sqlQuery = "UPDATE kevlarData " +
                "SET password=\"" + newPassword + "\"" +
                "WHERE Title=\"" + title + "\"";

        try (Connection connection = this.sqlConnect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            //Executes the statement
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * This function will search for a Title entered by user
     *
     * @param title Title entered by user to search for the title
     * @return validity Returns TRUE if the title is found or FALSE if not found
     */
    public Boolean checkForTitle(String title) {
        String sqlQuery = "SELECT Title FROM kevlarData";
        Boolean validity = false;
        try (Connection connection = this.sqlConnect()) {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sqlQuery);
            while (results.next()) {
                //checks if there is any similar values
                String databaseTitle = results.getString("Title");
                if (databaseTitle.equals(title)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return validity;
    }

    /**
     * Deletes the whole "userData.db" file after using it
     */
    public static void deleteData() {
        File dataBaseFile = new File("userData.db");
        dataBaseFile.delete();
    }
}
