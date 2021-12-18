package com.kevlar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.crypto.Cipher;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import static com.kevlar.DatabaseManager.base64TheFile;
import static com.kevlar.DatabaseManager.getHmac;


public class Connector {
    /**
     * the default constructor
     */
    public Connector() throws Exception {
    }


    /**
     * Takes the userInput and converts them to the XML format
     *
     * @param userName User's Username
     * @param password User's password
     */
    private String key = "";
    private byte[] decodedAES;
    private SecretKey aesKey;
    private IvParameterSpec initializationVectorSpec;

    public String userDataToXML(String userName, String password, String validationKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String encodedDatabase = base64TheFile();
        String hMac = getHmac(validationKey);
        String userDataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<hmac>" + hMac + "</hmac>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }

    public String newUserDataToXML(String userName, String password, String validationKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String encodedDatabase = base64TheFile();
        String userDataXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<validation>" + validationKey + "</validation>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }
    //Reference https://www.baeldung.com/java-aes-encryption-decryption

    public static IvParameterSpec generateIv() {

        byte[] iv = {(byte) 163, (byte) 127, (byte) 43, (byte) 227, 29, (byte) 181, (byte) 193, (byte) 101, (byte) 239, 2, (byte) 211, (byte) 149, (byte) 197, (byte) 37, (byte) 59, (byte) 83};
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public void setupConnection() {
        String connectionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        connectionXML += "<kevlar mode=\"handshake\">";
        connectionXML += "</kevlar>";
        Sender sender = new Sender(connectionXML, false);
        String response = sender.getResponse();
        System.out.println(response);
        response = response.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kevlar><key>b'", "");
        response = response.replace("'</key><iv>[", ",");
        response = response.replace("]</iv></kevlar>", "");

        String[] payload = response.split(",");
        byte[] ivData = new byte[payload.length - 1];

        for (int i = 1; i < payload.length; ++i)
            //ivData[i - 1] = (byte) Integer.parseInt(payload[i].strip());
            ivData[i - 1] = 0;

        key = payload[0];
        initializationVectorSpec = new IvParameterSpec(ivData);
        byte[] byteAES = key.getBytes();
        byte[] decodedAES = Base64.getDecoder().decode(byteAES);
        aesKey = new SecretKeySpec(decodedAES, 0, decodedAES.length, "AES");
    }

    public void encryptData(String userName, String password, File database, String hMac) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        String userData = userDataToXML(userName, password, hMac);
        byte[] userDataXML = userData.getBytes();
        // rebuild key using SecretKeySpec
        SecretKey originalAESKey = new SecretKeySpec(decodedAES, 0, decodedAES.length, "AES/CBC/PKCS5Padding");
        Cipher cipherMethod = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherMethod.init(Cipher.ENCRYPT_MODE, originalAESKey, initializationVectorSpec);
        byte[] cipherData = cipherMethod.doFinal(userDataXML);
        String ecryptedData = Base64.getEncoder().encodeToString(cipherData);
        Sender sender = new Sender(ecryptedData, true);
    }

    public Integer sendDataToServer(String userName, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String sendData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        sendData += "<kevlar mode=\"login\">";
        sendData += "<username>" + userName + "</username>";
        sendData += "<password>" + password + "</password>";
        sendData += "</kevlar>";
        byte[] bytesToSend = encryptAES(sendData);
        String finalData = Base64.getMimeEncoder().encodeToString(bytesToSend);
        System.out.println("final data" + " " + finalData);
        Sender sender = new Sender(finalData, true);
        String serverData = sender.getResponse();
        int responseLength = serverData.length();
        Document xmlDoc = convertStringToXMLDocument(serverData);
        NodeList kevlarDataByNode = xmlDoc.getElementsByTagName("kevlar");
        String serverUserData = "";
        String serverPassword = "";
        String serverDatabase = "";
        String serverHMac = "";
        int validationChecker = 4;
        for (int temp = 0; temp < kevlarDataByNode.getLength(); temp++) {
            Node kevlarNode = kevlarDataByNode.item(temp);
            if (kevlarNode.getNodeType() == Node.ELEMENT_NODE) {
                Element kevlarElement = (Element) kevlarNode;
                String id = kevlarElement.getAttribute("id");
                serverUserData = kevlarElement.getElementsByTagName("username").item(0).getTextContent();
                serverPassword = kevlarElement.getElementsByTagName("password").item(0).getTextContent();
                serverDatabase = kevlarElement.getElementsByTagName("database").item(0).getTextContent();
                serverHMac = kevlarElement.getElementsByTagName("hmac").item(0).getTextContent();
            }
            //returns0 if the data is not found in the server
            if ((serverUserData == "") && (serverPassword == "") && (serverDatabase == "") && (serverHMac == "")) {
                validationChecker = 0;

                //returns 1 if the data is found on the server AND matches the user's credentials
            } else if ((password.equals(serverPassword)) && (userName.equals(serverUserData))) {
                validationChecker = 1;
                //returns 2 if the password does not match with the server's password
            } else if ((!password.equals(serverPassword)) && (userName.equals(serverUserData))) {

                validationChecker = 2;
            }
        }
        return (validationChecker);


    }

    private byte[] encryptAES(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipherMethod = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherMethod.init(Cipher.ENCRYPT_MODE, aesKey, initializationVectorSpec);

        for (int i = 0; i < data.length() % 256; i++)
            data = ((byte) 0) + data;

        return cipherMethod.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }


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