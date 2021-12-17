package com.kevlar;

import java.util.Scanner;

public class Application {
	private Scanner scanner = new Scanner(System.in);
	private UserAccount userAccount;
	private Connector connector= new Connector();

	/**
	 * Default constructor.
	 */
	public Application() throws Exception {
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
	 * Show option after user has logged in.
	 */
	public void functionality() {
		boolean bShouldRun = true;
		while (bShouldRun) {
			printFunctionalMenu();
			switch (getCommand()) {
				case 1:
					viewPassword();
					break;

				case 2:
					addPassword();
					break;

				case 3:
					editPassword();
					break;

				case 4:
					editValidtionKey();
				break;

				case 0:
					bShouldRun = false;
					break;

				default:
					System.out.println("Please enter a valid command!");
			}
		}	
	}

	/**
	 * print the functional menu
	 */
	private void printFunctionalMenu(){
		printSeparator();
		System.out.println("Available commands: ");
		System.out.println("1. View Password.");
		System.out.println("2. Add new password.");
		System.out.println("3. Change Master Password.");
		System.out.println("4. Change Validation Key.");
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
	private void login() {
		String masterPassword,userName;

		printSeparator();

		//username & password send to thools have a while loop to get correct values
		System.out.println("\nEnter your Username: ");
		userName = scanner.nextLine();
		while (userName.length() < 5 || userName.length() > 30) {
			System.out.println("\nPlease enter a Username between 5 and 30 characters: ");
			userName = scanner.nextLine();
		}

		System.out.println("\nEnter Master Password: ");
		masterPassword = scanner.nextLine();
		while (masterPassword.length() < 8 || masterPassword.length() > 30) {
			System.out.println("\nPlease enter a Master Password between 8 and 30 characters: ");
			masterPassword = scanner.nextLine();
		}
		masterPassword = Hasher.getSHA256(masterPassword);

		/*
		System.out.println("\nEnter Validation key: ");
		String validationKey = scanner.nextLine();
		while (validationKey.length() < 8 || validationKey.length() > 30) {
			System.out.println("\nPlease enter a Validation key between 8 and 30 characters: ");
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
		String userName,masterPassword,validationKey;

		printSeparator();
		System.out.println("\nEnter your Username: ");
		userName = scanner.nextLine();
		while (userName.length() < 5 || userName.length() > 30) {
			System.out.println("\nPlease enter a Username between 5 and 30 characters: ");
			userName = scanner.nextLine();
		}
		masterPassword=ValidatePassword.validate("Master Password");
		System.out.println("\nA validation key is required to further the integrity of your password..");
		validationKey=ValidatePassword.validate("Validation key");
		userAccount = new UserAccount(userName,masterPassword,validationKey);
		functionality();
	}



	/**
	 * Index and decrypt a password.
	 */
	private void viewPassword() {
		printSeparator();
		// Logic goes here.
	}

	/**
	* Add a new password to the database
	* */
	private void addPassword(){
		String title,password;

		printSeparator();
		System.out.println("For what will this Password be stored for?: ");
		title= scanner.nextLine();
		password=ValidatePassword.validate("Password");
		password = AES.encrypt(password,userAccount.getMasterPassword(),userAccount.getUserName());

	}

	/**
	 * Enter a new password to the database.
	 */
	private void editPassword() {
		printSeparator();
		// Logic goes here.
	}

	/**
	 * Enter a new password to the database.
	 */
	private void editValidtionKey() {

		printSeparator();
	}
	

	/**
	 * Final function to cleanup the application upon exit.
	 */
	public void cleanup() {
		// Logic goes here.
	}


}
