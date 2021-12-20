package com.kevlar;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class PasswordIO {
    /**
     * Generate a random file name.
     *
     * @return The generated file name.
     */
    private static String generateFileName() {
        Random randomEngine = new Random();
        randomEngine.setSeed(new Date().getTime());

        String filename = "";
        final int max = randomEngine.nextInt(5, 16);
        for (int i = 0; i < max; i++)
            filename += String.valueOf(randomEngine.nextInt(0, 100));

        filename += ".txt";

        try {
            File createFile = new File(filename);
            createFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to open file!");
        }

        return filename;
    }


    /**
     * Get a user input using a text file.
     *
     * @return The input string.
     * @throws IOException This function could throw a IO exception.
     */
    public static String getInput() throws IOException {
        String filename = generateFileName();
        System.out.println("The file is located at: " + System.getProperty("user.dir") + "\\" + filename);
        System.out.print("Please hit enter after closing the text file.");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        FileInputStream file = new FileInputStream(filename);
        String input = new String(file.readAllBytes(), StandardCharsets.UTF_8);
        file.close();

        File fileToDelete = new File(filename);
        fileToDelete.delete();

        return input;
    }

    /**
     * Set output data to the user's clipboard.
     *
     * @param message The message to be sent.
     */
    public static void setOutput(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
        System.out.println("Password is copied to clipboard.");
    }

    /**
     * Set output data to a file and do not delete it (ignore it).
     *
     * @param message The message to set.
     * @throws IOException This function can throw an IO exception.
     */
    public static void setOutputIgnore(String message) throws IOException {
        String filename = generateFileName();
        FileOutputStream file = new FileOutputStream(filename);
        file.write(message.getBytes(StandardCharsets.UTF_8));
        file.close();
    }
}
