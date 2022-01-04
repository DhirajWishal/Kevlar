package com.kevlar;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class Application {
    /**
     * Run the application.
     *
     * @throws NoSuchAlgorithmException This function can throw a no such algorithm exception.
     * @throws InvalidKeyException      This function can throw an invalid key exception.
     * @throws IOException              this function can throw an IO exception.
     */
    public abstract void run() throws NoSuchAlgorithmException, InvalidKeyException, IOException;

    /**
     * Cleanup the application upon exit.
     */
    public abstract void cleanup();
}
