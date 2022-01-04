package com.kevlar;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public abstract class Application {
    protected final Scanner scanner = new Scanner(System.in);

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

    /**
     * Get a command from the user.
     * This function will loop until the user inputs a valid integer.
     *
     * @return The user entered integer.
     */
    protected int getCommand() {
        while (true) {
            try {
                System.out.print("Enter command: ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer!");
            }
        }
    }

    /**
     * Print a separator.
     */
    protected void printSeparator() {
        System.out.println("======================================");
    }
}
