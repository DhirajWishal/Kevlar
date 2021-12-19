package com.kevlar;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class UserAccount {
    private final String userName;
    private final String masterPassword;
    private String validationKey;
    private DatabaseManager databaseManager;
    private IvParameterSpec initializationVectorSpec;

    /**
     * Constructor.
     *
     * @param name The name of the user.S
     * @param mp   The master password.
     * @param vk   The validation key.
     */
    public UserAccount(String name, String mp, String vk) {
        this.userName = name;
        this.masterPassword = mp;
        this.validationKey = vk;
        this.databaseManager = new DatabaseManager();
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        this.initializationVectorSpec = new IvParameterSpec(iv);
    }

    /**
     * Constructor.
     *
     * @param name            The name of the user.S
     * @param mp              The master password.
     * @param vk              The validation key.
     * @param databaseManager The database manager.
     * @param ivData          The initialization vector data.
     */
    public UserAccount(String name, String mp, String vk, DatabaseManager databaseManager, byte[] ivData) {
        this.userName = name;
        this.masterPassword = mp;
        this.validationKey = vk;
        this.databaseManager = databaseManager;
        this.initializationVectorSpec = new IvParameterSpec(ivData);
    }

    /**
     * Utility function to get the username.
     *
     * @return The string.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Utility function to get the master password.
     *
     * @return The string.
     */
    public String getMasterPassword() {
        return this.masterPassword;
    }

    /**
     * Setter to set the validation key.
     *
     * @param validationKey The validation key to set.
     */
    public void setValidationKey(String validationKey) {
        this.validationKey = validationKey;
    }

    /**
     * Utility function to get the validation key.
     *
     * @return The string.
     */
    public String getValidationKey() {
        return this.validationKey;
    }


    /**
     * Utility function to get the database manager.
     *
     * @return The database manager.
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }


    /**
     * Get the initialization vector.
     *
     * @return The initialization vector.
     */
    public IvParameterSpec getInitializationVector() {
        return this.initializationVectorSpec;
    }

    /**
     * Encrypt a password using the master password.
     *
     * @param password The password to encrypt.
     * @return The encrypted string.
     */
    public String encrypt(String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), userName.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVectorSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
        }
        return null;
    }

    /**
     * Decrypt an encrypted password using the master password.
     *
     * @param password The password to decrypt.
     * @return The decrypted string.
     */
    public String decrypt(String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), userName.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVectorSpec);

            return new String(cipher.doFinal(Base64.getDecoder().decode(password)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
        }
        return null;
    }
}
