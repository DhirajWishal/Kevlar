package com.kevlar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class Application {
    /**
     * Run the application.
     *
     * @throws InvalidAlgorithmParameterException This function can throw an invalid algorithm parameter exception.
     * @throws NoSuchPaddingException             This function can throw a no such padding exception.
     * @throws IllegalBlockSizeException          This function can throw an illegal block size exception.
     * @throws NoSuchAlgorithmException           This function can throw a no such algorithm exception.
     * @throws BadPaddingException                This function can throw a bad padding exception.
     * @throws InvalidKeyException                This function can throw an invalid key exception.
     * @throws IOException                        this function can throw an IO exception.
     */
    public abstract void run() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException;

    /**
     * Cleanup the application upon exit.
     */
    public abstract void cleanup();
}
