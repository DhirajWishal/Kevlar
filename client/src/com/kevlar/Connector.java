package com.kevlar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
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
    private String key = "";
    private byte[] decodedAES;
    private SecretKey aesKey;
    private IvParameterSpec initializationVectorSpec;


    /**
     * the default constructor
     */
    public Connector() throws Exception {
    }

    /**
     *
     * @param userName
     * @param password
     * @param validationKey
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private String userDataToXML(String userName, String password, String validationKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String encodedDatabase = base64TheFile();
        String hMac = getHmac(validationKey);
        String userDataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"update\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<hmac>" + hMac + "</hmac>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }

    private String newUserDataToXML(String userName, String password, String validationKey, String ivData) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String encodedDatabase = base64TheFile();
        String userDataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<validation>" + validationKey + "</validation>";
        userDataXML += "<iv>" + ivData + "</iv>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }

    //Reference https://www.baeldung.com/java-aes-encryption-decryption
    public static IvParameterSpec generateIv() {

        byte[] iv = {(byte) 163, (byte) 127, (byte) 43, (byte) 227, 29, (byte) 181, (byte) 193, (byte) 101, (byte) 239, 2, (byte) 211, (byte) 149, (byte) 197, (byte) 37, (byte) 59, (byte) 83};
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * This function will update the SERVER's database on any password changes or username changes
     *
     * @param userName
     * @param password
     * @param validationKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public void sendExistingDataToServer(String userName, String password, String validationKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String userData = userDataToXML(userName, password, validationKey);
        Sender sender = new Sender(userData);
        System.out.println(sender.getResponse());   // TODO
    }

    /**
     * This function will send data to the server to MAKE a new entry
     *
     * @param userName
     * @param password
     * @param validationKey
     * @param ivData
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public void sendNewDataToServer(String userName, String password, String validationKey, String ivData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String userData = newUserDataToXML(userName, password, validationKey, ivData);
        Sender sender = new Sender(userData);
        System.out.println(sender.getResponse());   // TODO
    }


    /**
     * This function will check if the account already exists in the server
     * @param userName
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public Integer checkAccountExist(String userName, String password) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] validationkey = {0}; //temp var
        String sendData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        sendData += "<kevlar mode=\"check\">";
        sendData += "<username>" + userName + "</username>";
        sendData += "<password>" + password + "</password>";
        sendData += "</kevlar>";
        Sender sender = new Sender(sendData);
        String serverData = sender.getResponse();
        System.out.println(serverData);
        Document xmlDoc = convertStringToXMLDocument(serverData);
        NodeList kevlarDataByNode = xmlDoc.getElementsByTagName("kevlar");
        int stausCode = 4;
        for (int temp = 0; temp < kevlarDataByNode.getLength(); temp++) {
            Node kevlarNode = kevlarDataByNode.item(temp);
            if (kevlarNode.getNodeType() == Node.ELEMENT_NODE) {
                Element kevlarElement = (Element) kevlarNode;
                stausCode = Integer.parseInt(kevlarElement.getElementsByTagName("status").item(0).getTextContent());
            }
        }
        return (stausCode);
    }

    /**
     * This function will fetch the data from the server and return a userAccount object with all the data
     * @param userName
     * @param password
     * @param validationKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public UserAccount getUserAccount(String userName, String password, String validationKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String sendData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        sendData += "<kevlar mode=\"login\">";
        sendData += "<username>" + userName + "</username>";
        sendData += "<password>" + password + "</password>";
        sendData += "</kevlar>";
        Sender sender = new Sender(sendData);
        String serverData = sender.getResponse();
        int responseLength = serverData.length();
        Document xmlDoc = convertStringToXMLDocument(serverData);
        NodeList kevlarDataByNode = xmlDoc.getElementsByTagName("kevlar");
        String serverUserData = "";
        String serverPassword = "";
        String serverDatabase = "";
        String serverHMac = "";
        String serverIV = " ";
        int validationChecker = 4;
        UserAccount sendToApplication = null;
        for (int temp = 0; temp < kevlarDataByNode.getLength(); temp++) {
            Node kevlarNode = kevlarDataByNode.item(temp);
            if (kevlarNode.getNodeType() == Node.ELEMENT_NODE) {
                Element kevlarElement = (Element) kevlarNode;
                String id = kevlarElement.getAttribute("id");
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
            String finalHMACKey = Base64.getEncoder().encodeToString(byteHmac);
            if (finalHMACKey.equals(serverHMac)) {
                byte[] serverdataBase64 = (Base64.getDecoder().decode(serverDatabase));
                FileOutputStream databseFile = new FileOutputStream("userData.db");
                FileWriter writeToFile = new FileWriter("userData.db");
                databseFile.write(serverdataBase64);
                DatabaseManager databaseManager = new DatabaseManager();
                sendToApplication = new UserAccount(serverUserData, serverPassword, validationKey, databaseManager, ivAsBytes);
            }
        }
        return (sendToApplication);
    }

    /**
     * Converts String values to XML
     * @param xmlString
     * @return
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
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}