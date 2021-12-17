package com.kevlar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

public class Connector {
    private KeyPair theKeys = null;
    private static final String RSA = "RSA";
    private URL serverURL = new URL(" localhost:2255");
    private HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();


    public Connector() throws Exception {
        //Assigns KeyPairGenerator to generate using RSA
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(RSA);
        //Initializing the KeyPairGenerator
        keyGenerator.initialize(2048);
        //Generating the pair of keys
        theKeys = keyGenerator.generateKeyPair();
    }

    public void setupConnection() {
        publicKeyToXML();
    }


    public void getPublicKeyFromServer() throws Exception {
        connection.setRequestMethod("POST");
    }

    public String publicKeyToXML() {
        String publicKey = String.valueOf(theKeys.getPublic());
        String publicXML = "<?xml version=\"1.0\"encoding=\"UTF-8\"?>";
        publicXML += "<kevlar mode=\"handshake\">";
        publicXML += "<public keysize=\"2048\">" + publicKey + "</public>";
        publicXML += "</kevlar>";
        return (publicXML);
    }

    public void userDataToXML() {
        String userDataXML = "<?xml version=\"1.0\"encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username></username>";
        userDataXML += "<password></password>>";
        userDataXML += "<database></database>";
        userDataXML += "<hmac></hmac>";
        userDataXML += "</kevlar>";
    }

    public void requestFromServer() throws IOException {
        Map<String, String> xmlHashMap = new HashMap<>();
        xmlHashMap.put("Content Type", "text/xml");
        // connection.setDoOutput(true);
        //DataOutputStream outPutStream = new DataOutputStream(connection.getOutputStream());
        //outPutStream.writeBytes(hashMapStringBuilder.getxmlString(xmlHashMap));
        //outPutStream.flush();
        //outPutStream.close();

    }

    public class hashMapStringBuilder {
        public static String getxmlString(Map<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder hashText = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                hashText.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                hashText.append("=");
                hashText.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                hashText.append("&");
            }

            String finalString = hashText.toString();
            return finalString.length() > 0
                    ? finalString.substring(0, finalString.length() - 1)
                    : finalString;
        }
    }

    public void createDatabase() {
        Connection sqlConnector = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:userData.db";
            // create a connection to the database
            sqlConnector = DriverManager.getConnection(url);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (sqlConnector != null) {
                    sqlConnector.close();
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
                + "	Password text NOT NULL,\n"
                + ");";

        try (Connection databaseConnection = DriverManager.getConnection(url);
             Statement status = databaseConnection.createStatement()) {
            // create a new table
            status.execute(createSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}






