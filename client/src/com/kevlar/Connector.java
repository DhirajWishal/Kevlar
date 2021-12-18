package com.kevlar;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

import static com.kevlar.DatabaseManager.base64TheFile;
import static com.kevlar.DatabaseManager.getHmac;


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
        Cipher decrypt = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        decrypt.init(Cipher.DECRYPT_MODE, theKeys.getPrivate(), new OAEPParameterSpec("SHA-256",
                "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        // encryptedServerData gets the return data from handshakeWithServer()
        String encryptedServerData = handshakeWithServer();
        // decodedServerPublicKey is used to store and hold the base64 decoded data
        byte[] decodedServerPublicKey = Base64.getDecoder().decode(encryptedServerData);
        //decryptedPublicKey holds the decrypted data after which decodedServerPublicKey is decrypted using clients privateKey using RSA algorithm
        byte[] decryptedPublicKey = decrypt.doFinal(decodedServerPublicKey);
        String serverPublicKey = new String(decryptedPublicKey, StandardCharsets.UTF_8);
        System.out.println(serverPublicKey);
    }


    /**
     * This function will change the clients public key into the XML format and send it to the server
     *
     * @return PublicXML which contains the XML of the public key
     */
    public String publicKeyToXML() {
        String publicKey = new String(Base64.getMimeEncoder().encode(theKeys.getPublic().getEncoded()), StandardCharsets.UTF_8);
        String publicXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        publicXML += "<kevlar mode=\"handshake\">";
        publicXML += "<public keysize=\"2048\">"
                + "-----BEGIN RSA PRIVATE KEY-----\n"
                + publicKey
                + "\n-----END RSA PRIVATE KEY-----"
                + "</public>";
        publicXML += "</kevlar>";
        return (publicXML);
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
        String userDataXML = "<?xml version=\"1.0\"encoding=\"UTF-8\"?>";
        userDataXML += "<kevlar mode=\"account\">";
        userDataXML += "<username>" + userName + "</username>";
        userDataXML += "<password>" + password + "</password>>";
        userDataXML += "<database>" + encodedDatabase + "</database>";
        userDataXML += "<hmac>" + hMac + "</hmac>";
        userDataXML += "</kevlar>";
        return userDataXML;
    }
    //Reference https://www.baeldung.com/java-aes-encryption-decryption

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
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
        Sender sender = new Sender(EcryptedData, true);
    }
}