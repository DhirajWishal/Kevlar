package com.kevlar;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

public class PasswordIO {
    private String filename = "";

    /**
     * Default constructor.
     */
    public PasswordIO() {
        Random randomEngine = new Random();
        randomEngine.setSeed(new Date().getTime());

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
    }

    /**
     * Get a user input using a text file.
     *
     * @return The input string.
     * @throws IOException This function could throw a IO exception.
     */
    public String getInput() throws IOException {
        System.out.println("The file is located at: " + System.getProperty("user.dir") + "\\" + filename);
        System.out.print("Please hit enter after closing the text file.");
        System.in.read();

        FileInputStream file = new FileInputStream(filename);
        String input = new String(file.readAllBytes(), StandardCharsets.UTF_8);
        file.close();

        File fileToDelete = new File(filename);
        fileToDelete.delete();

        return input;
    }

    public void setOutput(String message) throws IOException {
        FileOutputStream file = new FileOutputStream(filename);
        file.write(message.getBytes(StandardCharsets.UTF_8));
        file.close();

        System.out.println("The file is located at: " + System.getProperty("user.dir") + "\\" + filename);
        System.out.print("Please hit enter after closing the text file.");

        File fileToDelete = new File(filename);
        fileToDelete.delete();
    }
}
