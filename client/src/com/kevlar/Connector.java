package com.kevlar;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;


public class Connector {
    /**
     * the default constructor
     */
    private KeyPair theKeys = null;
    private static final String RSA = "RSA";

    public Connector() throws Exception {
        //Assigns KeyPairGenerator to generate using RSA
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(RSA);
        //Initializing the KeyPairGenerator
        keyGenerator.initialize(2048);
        //Generating the pair of keys
        theKeys = keyGenerator.generateKeyPair();
    }

    /**
     * This Function will initiate the handshake with the function and gets the Server's Public key
     *
     * @return response ( The public key of the server which is base64 encoded )
     * @throws Exception
     */
    public String handshakeWithServer() throws Exception {
        //
        Sender sender = new Sender(Base64.getEncoder().encodeToString(publicKeyToXML().getBytes(StandardCharsets.UTF_8)), false);
        //
        String response = sender.getResponse();
        return (response);
    }

    /**
     * This function will take the base64 encoded data handshakeWithServer() and gets the public key
     *
     * @throws Exception
     */
    public void getServerPublicKey() throws Exception {
        Cipher decrypt = Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, theKeys.getPrivate());
        // encryptedServerData gets the return data from handshakeWithServer()
        String encryptedServerData = handshakeWithServer();
        // decodedServerPublicKey is used to store and hold the base64 decoded data
        byte[] decodedServerPublicKey = Base64.getDecoder().decode(encryptedServerData);
        //decryptedPublicKey holds the decrypted data after which decodedServerPublicKey is decrypted using clients privateKey using RSA algorithm
        byte[] decryptedPublicKey = decrypt.doFinal(decodedServerPublicKey);
        String serverPublicKey = new String(decryptedPublicKey, StandardCharsets.UTF_8);
    }


    /**
     * This function will change the clients public key into the XML format and send it to the server
     *
     * @return PublicXML which contains the XML of the public key
     */
    public String publicKeyToXML() {
        String publicKey = String.valueOf(theKeys.getPublic());
        String publicXML = "<?xml version=\"1.0\"encoding=\"UTF-8\"?>";
        publicXML += "<kevlar mode=\"handshake\">";
        publicXML += "<public keysize=\"2048\">" + publicKey + "</public>";
        publicXML += "</kevlar>";
        return (publicXML);
    }

    /**
     * Takes the userInput and converts them to the XML format
     *
     * @param userName User's Username
     * @param password User's password
     * @param database User's own database file
     * @param hMac     User's Validation Key
     */
    public void userDataToXML(String userName, String password, File database, String hMac) {
        String userDataXML = "<?xml version=\"1.0\"encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>>";
        userDataXML += "<database>" + database + "</database>";
        userDataXML += "<hmac>" + hMac + "</hmac>";
        userDataXML += "</kevlar>";
    }


}








