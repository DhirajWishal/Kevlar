package com.kevlar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import static com.kevlar.DatabaseManager.base64TheFile;
import static com.kevlar.DatabaseManager.getHmac;


public class Connector {
    /**
     * the default constructor
     */
    public Connector() {
    }

    /**
     * This function converts all the data taken in by the user and converts it to a XML format and returns the string
     * Mainly used to update the data in the server
     *
     * @param userName      User's Username
     * @param password      User's Password
     * @param validationKey User's Validation Key
     * @return The data in XML format
     * @throws IOException              make sure no I/O error has occurred
     * @throws NoSuchAlgorithmException makes sure the requested data is available
     * @throws InvalidKeyException      makes sure the key is in the correct format, length etc..
     */
    private String userDataToXML(String userName, String password, String validationKey) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException {
        //Calls the function base64TheFile which basically takes the whole "UserData.db" file and encodes it to base64
        String encodedDatabase = base64TheFile();
        //Calls the function getHmac which will take the base64Encoded file and the users validation key and generate the hMac
        String hMac = getHmac(validationKey);
        String userDataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        //Mode is set to update as the server is going to use this to update its content
        userDataXML += "<kevlar mode=\"update\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<hmac>" + hMac + "</hmac>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }

    /**
     * This function is the same as the earlier function BUT the difference here it doesn't send the encoded database
     * but instead sends the validation key to the server as the server doesn't contain the validation key
     *
     * @param userName      User's Username
     * @param password      User's Password
     * @param validationKey User's Validation Key
     * @param ivData        it is generated by the application
     * @return The data in XML format
     * @throws IOException make sure no I/O error has occurred
     */
    private String newUserDataToXML(String userName, String password, String validationKey, String ivData)
            throws IOException {
        String encodedDatabase = base64TheFile();
        String userDataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        //Mode is set to account which will be used to create a new entry
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<validation>" + validationKey + "</validation>";
        userDataXML += "<iv>" + ivData + "</iv>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }

    /**
     * This function will update the SERVER's database on any password changes or username changes, called when
     * server data wants to be updated
     *
     * @param userName      User's Username
     * @param password      User's Password
     * @param validationKey User's Validation Key
     * @throws IOException              make sure no I/O error has occurred
     * @throws NoSuchAlgorithmException makes sure the requested data is available
     * @throws InvalidKeyException      makes sure the key is in the correct format, length etc..
     */
    public void sendExistingDataToServer(String userName, String password, String validationKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String userData = userDataToXML(userName, password, validationKey);
        //an Instance of the Sender class is called here to send the data to the server
        Sender sender = new Sender(userData);

        /* FOR DEBUGGING
        String response = sender.getResponse();
        response = response.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kevlar><status>", "");
        response = response.replace("</status></kevlar>", "");
        */
    }

    /**
     * This function will send data to the server to MAKE a new entry on the server , called when an account is created
     *
     * @param userName      User's Username
     * @param password      User's Password
     * @param validationKey User's Validation Key
     * @param ivData        generated IV data
     * @throws IOException              make sure no I/O error has occurred
     * @throws NoSuchAlgorithmException makes sure the requested data is available
     * @throws InvalidKeyException      makes sure the key is in the correct format, length etc..
     */
    public void sendNewDataToServer(String userName, String password, String validationKey, String ivData)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String userData = newUserDataToXML(userName, password, validationKey, ivData);
        //an Instance of the Sender class is called here to send the data to the server
        Sender sender = new Sender(userData);

        /* FOR DEBUGGING
        String response = sender.getResponse();
        response = response.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kevlar><status>", "");
        response = response.replace("</status></kevlar>", "");
        */
    }


    /**
     * This function will check if the account already exists in the server, the data is sent to the server and the
     * server compares its data within itself and returns a Status code
     * if Status code = 0 ; there is no data in the server
     * if status code = 1; Username is there BUT the password is incorrect
     * if status code = 2; Username and password is on the server and matches
     *
     * @param userName User's Username
     * @param password User's Password
     * @return StatusCode
     */
    public Integer checkAccountExist(String userName, String password) {
        String sendData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        //Mode is set to check, as the username and password is used to check if it exists in the server
        sendData += "<kevlar mode=\"check\">";
        sendData += "<username>" + userName + "</username>";
        sendData += "<password>" + password + "</password>";
        sendData += "</kevlar>";

        //Sends it to the server
        Sender sender = new Sender(sendData);
        //Gets the servers response
        String serverData = sender.getResponse();

        Document xmlDoc = convertStringToXMLDocument(serverData);
        assert xmlDoc != null;
        //kevlarDataByNode used to extract the data from the XML from its tags
        NodeList kevlarDataByNode = xmlDoc.getElementsByTagName("kevlar");
        int stausCode = 4;

        for (int counter = 0; counter < kevlarDataByNode.getLength(); counter++) {
            Node kevlarNode = kevlarDataByNode.item(counter);
            //Sets the node get type to ELEMENT.NODE
            if (kevlarNode.getNodeType() == Node.ELEMENT_NODE) {
                Element kevlarElement = (Element) kevlarNode;
                //gets the value inside the tag <status> to a variable called statusCode
                stausCode = Integer.parseInt(kevlarElement.getElementsByTagName("status").item(0).getTextContent());
            }
        }
        return (stausCode);
    }

    /**
     * This function will fetch the data from the server and get the Username and all the details and assign them to
     * variables and then pass them to a userAccount object
     * and return it as a userAccount object
     *
     * @param userName      User's Username
     * @param password      User's Password
     * @param validationKey User's Validation Key
     * @return the Object userAccount
     * @throws IOException              make sure no I/O error has occurred
     * @throws NoSuchAlgorithmException makes sure the requested data is available
     * @throws InvalidKeyException      makes sure the key is in the correct format, length etc..
     */
    public UserAccount getUserAccount(String userName, String password, String validationKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String sendData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        //Mode set to login to get login details from the server
        sendData += "<kevlar mode=\"login\">";
        sendData += "<username>" + userName + "</username>";
        sendData += "<password>" + password + "</password>";
        sendData += "</kevlar>";
        //Sends the user's entered username and password
        Sender sender = new Sender(sendData);
        //Gets the serve's response

        String serverData = sender.getResponse();

        //Converts the whole of the response to XML data format
        Document xmlDoc = convertStringToXMLDocument(serverData);
        assert xmlDoc != null;
        NodeList kevlarDataByNode = xmlDoc.getElementsByTagName("kevlar");
        String serverUserData = "";
        String serverPassword = "";
        String serverDatabase = "";
        String serverHMac = "";
        String serverIV = " ";
        UserAccount sendToApplication = null;

        for (int temp = 0; temp < kevlarDataByNode.getLength(); temp++) {
            Node kevlarNode = kevlarDataByNode.item(temp);
            if (kevlarNode.getNodeType() == Node.ELEMENT_NODE) {
                Element kevlarElement = (Element) kevlarNode;
                /**
                 * serverUserData gets the data stored in tag <username></username>
                 * serverPassword gets the data stored in tag <password></password>
                 * serverDatabase gets the data stored in tag <database></database>
                 * serverHMac gets the data stored in tag <hmac></hmac>
                 * serverIV gets the data stored in tag <iv></iv>
                 */
                serverUserData = kevlarElement.getElementsByTagName("username").item(0).getTextContent();
                serverPassword = kevlarElement.getElementsByTagName("password").item(0).getTextContent();
                serverDatabase = kevlarElement.getElementsByTagName("database").item(0).getTextContent();
                serverHMac = kevlarElement.getElementsByTagName("hmac").item(0).getTextContent();
                serverIV = kevlarElement.getElementsByTagName("iv").item(0).getTextContent();
            }
            byte[] ivAsBytes = Base64.getDecoder().decode(serverIV.getBytes());
            byte[] validationBytes = validationKey.getBytes();
            byte[] database64Decoded = Base64.getDecoder().decode(serverDatabase);

            SecretKeySpec secretKeySpec = new SecretKeySpec(validationBytes, "SHA-256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] byteHmac = mac.doFinal(serverDatabase.getBytes(StandardCharsets.UTF_8));
            //Generates the HMAC key from using the validation key of the user and the serverDatabase
            String finalHMACKey = Base64.getEncoder().encodeToString(byteHmac);

            //IF the server's Hmac and the generated Hmac is similar that means the data is correct and there is no deviation
            if (finalHMACKey.equals(serverHMac)) {
                //Opens "userData.db" as an output stream and writes the serverDatabase after decryption
                FileOutputStream databaseFile = new FileOutputStream("userData.db");
                databaseFile.write(database64Decoded);
                databaseFile.close();
                DatabaseManager databaseManager = new DatabaseManager();
                sendToApplication = new UserAccount(
                        new String(Base64.getDecoder().decode(serverUserData), StandardCharsets.UTF_8),
                        new String(Base64.getDecoder().decode(serverPassword), StandardCharsets.UTF_8),
                        new String(Base64.getDecoder().decode(validationKey), StandardCharsets.UTF_8),
                        databaseManager, ivAsBytes);
            }
        }
        return (sendToApplication);
    }

    /**
     * Converts String values to XML
     *
     * @param xmlString the string to be converted to Doc
     * @return the Doc
     */
    //Reference https://howtodoinjava.com/java/xml/parse-string-to-xml-dom/
    private static Document convertStringToXMLDocument(String xmlString) {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}