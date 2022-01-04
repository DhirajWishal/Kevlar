package com.kevlar.vault;

import com.kevlar.Application;

public class Vault extends Application {
    /**
     * Run the application.
     */
    @Override
    public void run() {
        boolean bShouldRun = true;
        while (bShouldRun) {
            printMenu();
        }
    }

    /**
     * Print the menu.
     */
    private void printMenu() {
        printSeparator();
        System.out.println("1. Encrypt file.");
        System.out.println("2. Decrypt file.");
        System.out.println("0. Exit.");
        printSeparator();
    }

    /**
     * Cleanup the application upon exit.
     */
    @Override
    public void cleanup() {

    }
}
