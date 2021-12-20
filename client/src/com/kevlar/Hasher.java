package com.kevlar;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    /**
	 * Reference : https://www.geeksforgeeks.org/md5-hash-in-java/
     * hashes the values using SHA-256
	 */
    public static String getSHA256(String value)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(value.getBytes());
            BigInteger num = new BigInteger(1, messageDigest);
            String hashText = num.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } 
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}


