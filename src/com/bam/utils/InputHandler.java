package com.bam.utils;
import com.bam.exceptions.*;
import com.bam.models.Account;
import com.bam.services.AccountManager;

import java.util.Scanner;

// performs input handling
public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);
    private final InputValidator validator = new InputValidator();

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
            age = getIntInput(prompt, "Age must be a number");
            try {
                validator.validateAge(age);
                break; // Valid input, exit loop
            } catch (InvalidAgeException e) {
                System.out.println(e.getMessage());
            }
        }
        return age;
    }

    public String getName(String prompt) {
        String name;
        while (true) {
            name = getStringInput(prompt);
            try {
                validator.validateName(name);
                break; // Valid input, exit loop
            } catch (InvalidNameException e) {
                System.out.println(e.getMessage());
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
                System.out.println(e.getMessage());
            }
        }
    }

    public String getContact(String prompt) {
        String contact;
        while (true) {
            contact = getStringInput(prompt);
            try {
                validator.validateContact(contact);
                break; // Valid input, exit loop
            } catch (InvalidContactException e) {
                System.out.println(e.getMessage());
            }
        }
        return contact;
    }

    public double getInitialDeposit(String prompt, String accountType) {
        double depositAmount;
        while (true) {
            depositAmount = getDoubleInput(prompt, "Deposit must be a number");
            try{
                validator.validateInitialDepositAmount(depositAmount, accountType);
                break;
            }
            catch(InsufficientInitialDepositException e){
                System.out.println(e.getMessage());
            }
        }
        return depositAmount;
    }

    public int getAccountTypeChoice(String prompt) {
        int choice;
        while (true) {
            choice = getIntInput(prompt, "Choice must be a number");
            try{
                validator.validateAccountTypeChoice(choice);
                break; // Valid input, exit loop
            }
            catch(InvalidChoiceException e){
                System.out.println(e.getMessage());
            }
        }
        return choice;
    }

    public int getCustomerTypeChoice(String prompt) {
        int choice;
        while (true) {
            choice = getIntInput(prompt, "Choice must be a number");
            try{
                validator.validateCustomerTypeChoice(choice);
                break; // Valid input, exit loop
            }
            catch(InvalidChoiceException e){
                System.out.println(e.getMessage());
            }
        }
        return choice;
    }

    public int getTransactionTypeChoice(String prompt) {
        int choice;
        while (true) {
            choice = getIntInput(prompt, "Choice must be a number");
            try{
                validator.validateTransactionTypeChoice(choice);
                break; // Valid input, exit loop
            }
            catch(InvalidChoiceException e){
                System.out.println(e.getMessage());
            }
        }
        return choice;
    }

    public double getDepositAmount(String prompt) {
        double amount;
        while (true) {
            amount = getDoubleInput(prompt, "Amount must be a number");
            try{
                validator.validateDepositAmount(amount);
                break; // Valid input, exit loop
            }
            catch(InvalidDepositAmountException e){
                System.out.println(e.getMessage());
            }
        }
        return amount;
    }

    public double getWithdrawalAmount(String prompt, Account account) {
        double amount;
        while (true) {
            amount = getDoubleInput(prompt, "Amount must be a number");
            try{
                validator.validateWithdrawalAmount(amount, account);
                break; // Valid input, exit loop
            }
            catch(InsufficientFundsException | InvalidWithdrawalAmountException | OverdraftExceededException e){
                System.out.println(e.getMessage());
            }
        }
        return amount;
    }

    public void waitForEnter() {
        scanner.nextLine();
    }

    public void closeScanner() {
        scanner.close();
    }

}
