package com.kevlar.vault;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class FileEncryptor {
    private final FileInputStream inputFile;
    private final FileOutputStream outputFile;
    private final String outputFileName;

    /**
     * Constructor.
     *
     * @param file The file to open.
     * @throws FileNotFoundException This constructor can throw a file not found exception.
     */
    public FileEncryptor(File file) throws FileNotFoundException {
        inputFile = new FileInputStream(file);
        outputFileName = file.getAbsolutePath()
                + Arrays.toString(Base64.getEncoder().encode(file.getName().getBytes(StandardCharsets.UTF_8)));
        outputFile = new FileOutputStream(outputFileName);
    }

    /**
     * Encrypt the file.
     *
     * @param initializationVector The initialization vector to use.
     * @param password             The password to encrypt with.
     * @param salt                 The salt used by the encryption algorithm.
     */
    public void encrypt(IvParameterSpec initializationVector, String password, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector);
            outputFile.write(cipher.doFinal(inputFile.readAllBytes()));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
        }
    }

    /**
     * Get the output file name.
     *
     * @return The file name.
     */
    public String getOutputFileName() {
        return outputFileName;
    }
}
