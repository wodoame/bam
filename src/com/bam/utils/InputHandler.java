package com.bam.utils;
import com.bam.exceptions.InvalidAccountException;
import com.bam.models.Account;
import com.bam.services.AccountManager;

import java.util.Scanner;

// performs input handling and validation
public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);

    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public double getDoubleInput(String prompt, String errorMessage) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print(errorMessage + '\n');
            System.out.print(prompt);
            scanner.next(); // consume bad input
        }
        double input = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return input;
    }


    public int getIntInput(String prompt, String errorMessage) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print(errorMessage + '\n');
            System.out.print(prompt);
            scanner.next(); // consume bad input
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return input;
    }

    public int getAge(String prompt) {
        int age;
        while (true) {
            age = getIntInput(prompt, "");
            if (age < 18) {
                System.out.println("You must be at least 18 years old to create an account.");
            } else {
                break;
            }
        }
        return age;
    }

    public String getName(String prompt) {
        String name;
        while (true) {
            name = getStringInput(prompt);
            if (name.trim().isEmpty() || !name.matches("[a-zA-Z ]+")) {
                System.out.println("Invalid name. Please enter a valid name containing only letters and spaces.");
            } else {
                break;
            }
        }
        return name;
    }

    public Account getAccount(String prompt, AccountManager acm) {
        String accountNumber;
        Account account;
        while (true) {
            accountNumber = getStringInput(prompt);
            try {
                 account = acm.findAccount(accountNumber);
                 return account;
            } catch (InvalidAccountException e) {
                System.out.println("Account not found. Please check the account number and try again.");
            }
        }
    }

    public void closeScanner() {
        scanner.close();
    }

}
