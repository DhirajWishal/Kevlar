package com.kevlar;

import java.util.Scanner;

public class Application {
	private Scanner scanner = new Scanner(System.in);

	/**
	 * Default constructor.
	 */
	public Application() {
		System.out.println("Welcome to Kevlar!");
	}

	/**
	 * Run the main application loop.
	 */
	public void run() {
		boolean bShouldRun = true;
		while (bShouldRun) {
			printMenu();
			switch (getCommand()) {
				case 1:
					login();
					break;

				case 2:
					createAccount();
					break;

				case 3:
					decryptPassword();
					break;

				case 4:
					enterNewPassword();
					break;

				case 99:
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
	private void printMenu() {
		printSeparator();
		System.out.println("Available commands: ");
		System.out.println("1. Login to system.");
		System.out.println("2. Create new account.");
		System.out.println("3. Decrypt password.");
		System.out.println("4. Enter a new password password.");
		System.out.println("99. Exit program.");
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
	private void login() {
		printSeparator();
		// Logic goes here.
	}

	/**
	 * Create a new user account.
	 */
	private void createAccount() {
		printSeparator();
		// Logic goes here.
	}

	/**
	 * Index and decrypt a password.
	 */
	private void decryptPassword() {
		printSeparator();
		// Logic goes here.
	}

	/**
	 * Enter a new password to the database.
	 */
	private void enterNewPassword() {
		printSeparator();
		// Logic goes here.
	}

	/**
	 * Final function to cleanup the application upon exit.
	 */
	public void cleanup() {
		// Logic goes here.
	}
}
