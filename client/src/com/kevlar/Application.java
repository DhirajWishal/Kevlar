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
			printLoginMenu();
			switch (getCommand()) {
				case 1:
					login();
					break;

				case 2:
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
	 * print the functional menu
	 */
	private void printFunctionalMenu(){
		printSeparator();
		System.out.println("Available commands: ");
		System.out.println("1. View Password.");
		System.out.println("2. Change Main Password.");
		System.out.println("3. Change Validation Key.");
		System.out.println("0. Logout & exit application.");
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
		String masterPassword,userName,validationKey;

		printSeparator();

		//username & password send to thools have a while loop to get correct values
		System.out.println("Enter your Username: ");
		userName = scanner.nextLine();
		while (userName.length() < 5 || userName.length() > 30) {
			System.out.println("Please enter a Username between 5 and 30 characters: ");
			userName = scanner.nextLine();
		}

		System.out.println("Enter Master Password: ");
		masterPassword = scanner.nextLine();
		while (masterPassword.length() < 8 || masterPassword.length() > 30) {
			System.out.println("Please enter a Master Password between 8 and 30 characters: ");
			masterPassword = scanner.nextLine();
		}
		masterPassword = Hasher.getSHA256(masterPassword);

		/*
		System.out.println("Enter Validation key: ");
		validationKey = scanner.nextLine();
		while (validationKey.length() < 8 || validationKey.length() > 30) {
			System.out.println("Please enter a Validation key between 8 and 30 characters: ");
			validationKey = scanner.nextLine();
		}
		validationKey = Hasher.getSHA256(validationKey);
		*/
	}

	/**
	 * Create a new user account.
	 */
	private void createAccount() {
		boolean bConfirm = false;
		String userName,masterPassword="",confirmPassword,validationKey="";

		printSeparator();
		System.out.println("Enter your Username: ");
		userName = scanner.nextLine();
		while (userName.length() < 5 || userName.length() > 30) {
			System.out.println("Please enter a Username between 5 and 30 characters: ");
			userName = scanner.nextLine();
		}
		while (bConfirm == false) {
			System.out.println("Enter Master Password: ");
			masterPassword = scanner.nextLine();
			while (masterPassword.length() < 8 || masterPassword.length() > 30) {
				System.out.println("Please enter a Master Password between 8 and 30 characters: ");
				masterPassword = scanner.nextLine();
			}
			System.out.println("Confirm Password: ");
			confirmPassword = scanner.nextLine();
			if (confirmPassword.equals(masterPassword)) {
				System.out.println("Username & Password Created Succesfully!");
				masterPassword=Hasher.getSHA256(masterPassword);
				bConfirm=true;
			}else{
				System.out.println("Password Mismatch please Re-enter!!");
			}
		}
		bConfirm=false;
		while (bConfirm == false) {
			System.out.println("Enter a Validation key(Required to further the integrity of the Database): ");
			validationKey = scanner.nextLine();
			while (validationKey.length() < 8 || validationKey.length() > 30) {
				System.out.println("Please enter a Validation key between 8 and 30 characters: ");
				validationKey = scanner.nextLine();
			}
			System.out.println("Confirm validation key: ");
			confirmPassword = scanner.nextLine();
			if (confirmPassword.equals(validationKey)) {
				System.out.println("Validation key Created Succesfully!");
				validationKey=Hasher.getSHA256(validationKey);
				bConfirm=true;
			}else{
				System.out.println("Validation key Mismatch please Re-enter!!");
			}
		}
		
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
