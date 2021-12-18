package com.kevlar;

import java.util.Scanner;

/**
 * validate passwords with double entry and length check
 */
public class ValidatePassword {

    public static String validate(String type){
        Scanner scanner = new Scanner(System.in);
        boolean bConfirm = false;
        String value="";
        String confirm;

        while (bConfirm == false) {
            System.out.println("\nEnter your "+type+": ");
            value = scanner.nextLine();
            while (value.length() < 8 || value.length() > 30) {
                System.out.println("\nPlease enter a " + type + " between 8 and 30 characters: ");
                value = scanner.nextLine();
            }
            System.out.println("\nConfirm Password: ");
            confirm = scanner.nextLine();
            if (confirm.equals(value)) {
                System.out.println("\n" + type + " Created Succesfully!");
                bConfirm = true;
            } else {
                System.out.println("\n" + type + " Mismatch please Re-enter!!");
            }
        }
        return value;
    }
}
