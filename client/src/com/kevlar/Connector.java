package com.kevlar;

import javax.crypto.Cipher;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
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
        Sender sender = new Sender(connectionXML);
        String response = sender.getResponse();
        System.out.println(response);
    }

    public void encryptData(String userName, String password, File database, String hMac, String serverAESKey, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        String userData = userDataToXML(userName, password, hMac);
        byte[] userDataXML = userData.getBytes();
        byte[] decodedAESKey = Base64.getDecoder().decode(serverAESKey);
        // rebuild key using SecretKeySpec
        SecretKey originalAESKey = new SecretKeySpec(decodedAESKey, 0, decodedAESKey.length, "AES");


        Cipher cipherMethod = Cipher.getInstance("AES");
        cipherMethod.init(Cipher.ENCRYPT_MODE, originalAESKey, iv);
        byte[] cipherData = cipherMethod.doFinal(userDataXML);
        String EcryptedData = Base64.getEncoder().encodeToString(cipherData);
        Sender sender = new Sender(EcryptedData);
    }

    public void sendDataToServer(String userName, String password) {
        String sendData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        sendData += "<kevlar mode=\"login\">";
        sendData += "<username>" + userName + "</username>";
        sendData += "<password>" + password + "</password>>";
        sendData += "</kevlar>";
        Sender sender = new Sender(sendData);
        String serverData = sender.getResponse();
        int responseLength = serverData.length();

    }

}