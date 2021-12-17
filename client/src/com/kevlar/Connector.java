package com.kevlar;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;


import java.util.Map;


public class Connector {
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

    public void setupConnection() {
        publicKeyToXML();
    }


    public String handshakeWithServer() throws Exception {
        Sender sender = new Sender(Base64.getEncoder().encodeToString(publicKeyToXML().getBytes(StandardCharsets.UTF_8)), false);
        String response = sender.getResponse();
        return (response);
    }
    public void getServerPublicKey() throws Exception {
        Cipher decrypt = Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, theKeys.getPrivate());
        String encryptedServerData=handshakeWithServer();
        byte[] decodedServerPublicKey=Base64.getDecoder().decode(encryptedServerData);
        byte[] decryptedPublicKey= decrypt.doFinal(decodedServerPublicKey);
        String serverPublicKey=new String(decryptedPublicKey, StandardCharsets.UTF_8);
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




}








