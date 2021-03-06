package com.kevlar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;

public class Application {
    private final Scanner scanner = new Scanner(System.in);
    private UserAccount userAccount;
    private final Connector connector = new Connector();

    /**
     * Default constructor.
     */
    public Application() {
        System.out.println("Welcome to Kevlar!");
    }

    /**
     * Run the main application loop.
     */
    public void run() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        boolean bShouldRun = true;
        while (bShouldRun) {
            printLoginMenu();
            switch (getCommand()) {
                case 1:
                    login();
                    break;

                case 2:
                    DatabaseManager.deleteData();
                    createAccount();
                    break;

                case 0:
                    bShouldRun = false;
                    break;

                default:
                    System.out.println("Please enter a valid command!");
            }
        }

        System.out.println("Thank you for using Kevlar!");
    }

    /**
     * Print the menu.
     */
    private void printLoginMenu() {
        printSeparator();
        System.out.println("Available commands: ");
        System.out.println("1. Login to system.");
        System.out.println("2. Create new account.");
        System.out.println("0. Exit program.");
        printSeparator();
    }

    /**
     * Show option after user has logged in.
     */
    public void functionality() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        boolean bShouldRun = true;

        while (bShouldRun) {
            printSeparator();
            System.out.println("Title     Description");
            printSeparator();
            userAccount.getDatabaseManager().getTitleDescription();
            printSeparator();
            System.out.println("\n");
            printFunctionalMenu();
            switch (getCommand()) {
                case 1:
                    viewPassword();
                    break;

                case 2:
                    addPassword();
                    break;

                case 3:
                    editpassword();
                    break;

                case 4:
                    editMasterPassword();
                    break;

                case 0:
                    bShouldRun = false;
                    userAccount = null;
                    DatabaseManager.deleteData();
                    break;

                default:
                    System.out.println("Please enter a valid command!");
            }
        }
    }

    /**
     * print the functional menu
     */
    private void printFunctionalMenu() {
        printSeparator();
        System.out.println("Available commands: ");
        System.out.println("1. View Password.");
        System.out.println("2. Add new password.");
        System.out.println("3. Change a stored password.");
        System.out.println("4. Change Master Password.");
        System.out.println("0. Logout of application.");
        printSeparator();
    }

    /**
     * Get a command from the user.
     * This function will loop until the user inputs a valid integer.
     *
     * @return The user entered integer.
     */
    private int getCommand() {
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
    private void printSeparator() {
        System.out.println("======================================");
    }

    /**
     * Login to the kevlar system.
     */

    private void login() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        String masterPassword, userName, base64un, base64mp, base64vk, exit = "", validationKey;
        Integer checker;
        boolean bverify;

        printSeparator();

        System.out.println("\nEnter your Username: ");
        userName = scanner.nextLine();
        while (userName.length() < 5 || userName.length() > 30) {
            System.out.println("\nPlease enter a Username between 5 and 30 characters: ");
            userName = scanner.nextLine();
        }

        System.out.println("\nEnter Master Password: ");
        masterPassword = PasswordIO.getInput();
        masterPassword = Hasher.getSHA256(masterPassword);

        base64un = Base64.getEncoder().encodeToString(userName.getBytes());
        base64mp = Base64.getEncoder().encodeToString(masterPassword.getBytes());
        checker = connector.checkAccountExist(base64un, base64mp);

        bverify = verifyLogin(checker, userName, masterPassword);

        if (bverify) {
            System.out.println("\nEnter Validation key: ");
            validationKey = PasswordIO.getInput();

            validationKey = Hasher.getSHA256(validationKey);
            userName = userAccount.getUserName();
            masterPassword = userAccount.getMasterPassword();


            base64vk = Base64.getEncoder().encodeToString(validationKey.getBytes());
            base64un = Base64.getEncoder().encodeToString(userName.getBytes());
            base64mp = Base64.getEncoder().encodeToString(masterPassword.getBytes());
            try {
                userAccount = connector.getUserAccount(base64un, base64mp, base64vk);
            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                System.out.println(e.getMessage());
            }

            if (userAccount != null)
                functionality();
            else
                System.out.println("Invalid login!");
        }
    }

    /**
     * Create a new user account.
     */
    private void createAccount() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        boolean bConfirm = false;
        Integer checker;
        String userName, masterPassword, validationKey, base64un, base64mp, base64vk;

        printSeparator();
        System.out.println("\nEnter your Username: ");
        userName = scanner.nextLine();
        while (userName.length() < 5 || userName.length() > 30) {
            System.out.println("\nPlease enter a Username between 5 and 30 characters: ");
            userName = scanner.nextLine();
        }
        base64un = Base64.getEncoder().encodeToString(userName.getBytes());
        checker = connector.checkAccountExist(base64un, "");
        while (checker > 0) {
            System.out.println("\n" + userName + " already exists! please re:enter new username ");
            userName = scanner.nextLine();
            while (userName.length() < 5 || userName.length() > 30) {
                System.out.println("\nPlease enter a Username between 5 and 30 characters: ");
                userName = scanner.nextLine();
            }
            base64un = Base64.getEncoder().encodeToString(userName.getBytes());
            checker = connector.checkAccountExist(base64un, "");

        }

        System.out.println("Enter Your Master Password: ");
        masterPassword = PasswordIO.getInput();
        System.out.println("\nA validation key is required to further the integrity of your password..");
        validationKey = PasswordIO.getInput();
        masterPassword = Hasher.getSHA256(masterPassword);
        validationKey = Hasher.getSHA256(validationKey);
        userAccount = new UserAccount(userName, masterPassword, validationKey);

        //encode in base64 and send to server

        base64mp = Base64.getEncoder().encodeToString(masterPassword.getBytes());
        base64vk = Base64.getEncoder().encodeToString(validationKey.getBytes());
        userAccount.getDatabaseManager().createTable();
        try {
            connector.sendNewDataToServer(base64un, base64mp, base64vk, Base64.getEncoder().encodeToString(userAccount.getInitializationVector().getIV()));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println(e.getMessage());
        }

        userAccount.getDatabaseManager().createTable();
        functionality();
    }


    /**
     * Index and decrypt a password.
     */
    private void viewPassword() throws IOException {
        String title, password, username;

        printSeparator();
        System.out.println("Which account password would you like to view?");
        title = scanner.nextLine();
        username = userAccount.getDatabaseManager().getUserName(title);
        while (username == null && !title.equals("-1")) {
            System.out.println("Please enter an appropriate account title(-1 to exit)");
            title = scanner.nextLine();
            username = userAccount.getDatabaseManager().getUserName(title);
        }
        password = userAccount.getDatabaseManager().getPassword(title);
        if (password != null) {
            password = userAccount.decrypt(password);
            System.out.println("Your Username: " + username);
            PasswordIO.setOutput(password);
        } else {
            System.out.println("Exiting view password...");
        }
    }

    /**
     * Add a new password to the database
     */
    private void addPassword() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        String title, password, titleUsername, description;

        printSeparator();
        System.out.println("For what account will this Password be stored for?: ");
        title = scanner.nextLine();
        System.out.println("Add a small description (could be a hint to be displayed alongside the prior entered title):  ");
        description = scanner.nextLine();
        System.out.println("What username did you use for this account?: ");
        titleUsername = scanner.nextLine();
        System.out.println("Enter your password: ");
        password = PasswordIO.getInput();
        password = userAccount.encrypt(password);
        userAccount.getDatabaseManager().insertData(title, titleUsername, description, password);

        connector.sendExistingDataToServer(Base64.getEncoder().encodeToString(userAccount.getUserName().getBytes()),
                Base64.getEncoder().encodeToString(userAccount.getMasterPassword().getBytes()),
                Base64.getEncoder().encodeToString(userAccount.getValidationKey().getBytes()));
    }

    /**
     * Change currently stored password
     */
    private void editpassword() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        String title, password;
        boolean bexists;

        printSeparator();
        System.out.println("Which account password do you wish to change?: ");
        title = scanner.nextLine();
        bexists = userAccount.getDatabaseManager().checkForTitle(title);
        while (!bexists && !title.equals("-1")) {
            System.out.println("Please enter proper account name!(-1 to exit)");
            title = scanner.nextLine();
            bexists = userAccount.getDatabaseManager().checkForTitle(title);
        }
        if (!title.equals("-1")) {
            System.out.println("Type in previously entered password: ");
            password = scanner.nextLine();
            password = userAccount.encrypt(password);
            bexists = userAccount.getDatabaseManager().checkForPassword(title, password);
            while (!bexists && !password.equals("-1")) {
                System.out.println("Password incorrect Re-Enter!(-1 to exit)");
                password = scanner.nextLine();
                password = userAccount.encrypt(password);
                bexists = userAccount.getDatabaseManager().checkForPassword(title, password);
            }
            if (!password.equals("-1")) {
                System.out.println("Enter your password: ");
                password = PasswordIO.getInput();
                password = userAccount.encrypt(password);
                userAccount.getDatabaseManager().changePassword(title, password);
                System.out.println("Password changed successfully!!");
            } else {
                System.out.println("Password change failed User could not remember past password");
            }

        } else {
            System.out.println("Password change failed invalid account name");
        }
        connector.sendExistingDataToServer(Base64.getEncoder().encodeToString(userAccount.getUserName().getBytes()),
                Base64.getEncoder().encodeToString(userAccount.getMasterPassword().getBytes()),
                Base64.getEncoder().encodeToString(userAccount.getValidationKey().getBytes()));
    }

    /**
     * Change the current master password
     */
    public void editMasterPassword() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        ArrayList<String> titles = userAccount.getDatabaseManager().queryTitles();

        // Get the old master password.
        System.out.println("Enter old master password.");
        String masterPassword = Hasher.getSHA256(PasswordIO.getInput());

        // Check if the passwords match.
        if (!masterPassword.equals(userAccount.getMasterPassword())) {
            System.out.println("The master passwords do not match!");
            return;
        }


        // Get the new password.
        System.out.println();
        System.out.println("Enter new master password.");
        String newMasterPassword = Hasher.getSHA256(PasswordIO.getInput());

        // Iterate through the existing records.
        for (String title : titles) {
            // Get the old password.
            String oldPassword = userAccount.getDatabaseManager().getPassword(title);

            // Decrypt the old password using the old master key, and then encrypt it again using the new key.
            String newPassword = UserAccount.encryptOnce(userAccount.decrypt(oldPassword), userAccount.getUserName(),
                    newMasterPassword, userAccount.getInitializationVector());

            // Update the database.
            userAccount.getDatabaseManager().changePassword(title, newPassword);
        }

        // Set the master password and notify the server.
        userAccount.setMasterPassword(newMasterPassword);
        connector.sendExistingDataToServer(Base64.getEncoder().encodeToString(userAccount.getUserName().getBytes()),
                Base64.getEncoder().encodeToString(userAccount.getMasterPassword().getBytes()),
                Base64.getEncoder().encodeToString(userAccount.getValidationKey().getBytes()));
    }

    /**
     * verify password and username & check if they are there
     */
    public boolean verifyLogin(Integer checker, String userName, String masterPassword) throws IOException {
        String base64un, base64mp, leaver;

        base64un = Base64.getEncoder().encodeToString(userName.getBytes());
        while (true) {
            switch (checker) {
                case 0:
                    System.out.println("Account does not exists do you perhaps wish to create a new Account instead?(Yes/No/Y/N)");
                    leaver = scanner.nextLine();
                    if (leaver.equalsIgnoreCase("yes") || leaver.equalsIgnoreCase("y")) {
                        return false;
                    }
                    System.out.println("\nEnter your Username: ");
                    userName = scanner.nextLine();
                    while (userName.length() < 5 || userName.length() > 30) {
                        System.out.println("\nPlease enter a Username between 5 and 30 characters: ");
                        userName = scanner.nextLine();
                    }

                    System.out.println("\nEnter Master Password: ");
                    masterPassword = PasswordIO.getInput();
                    masterPassword = Hasher.getSHA256(masterPassword);

                    base64un = Base64.getEncoder().encodeToString(userName.getBytes());
                    base64mp = Base64.getEncoder().encodeToString(masterPassword.getBytes());
                    checker = connector.checkAccountExist(base64un, base64mp);
                    break;
                case 1:
                    System.out.println("Invalid password entered do you wish to create a new Account instead?(Yes/No/Y/N)");
                    leaver = scanner.nextLine();
                    if (leaver.equalsIgnoreCase("yes") || leaver.equalsIgnoreCase("y")) {
                        return false;
                    }
                    System.out.println("\nInvalid password entered please Re-enter password!");
                    masterPassword = PasswordIO.getInput();
                    masterPassword = Hasher.getSHA256(masterPassword);
                    base64mp = Base64.getEncoder().encodeToString(masterPassword.getBytes());
                    checker = connector.checkAccountExist(base64un, base64mp);
                    break;
                case 2:
                    userAccount = new UserAccount(userName, masterPassword);
                    return true;
                default:
                    checker = 0;
            }
        }
    }

    /**
     * Final function to clean up the application upon exit.
     */
    public void cleanup() {
        userAccount = null;
        DatabaseManager.deleteData();
    }
}
