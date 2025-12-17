package com.bam.utils;
import com.bam.exceptions.*;
import com.bam.models.Account;
import com.bam.services.AccountManager;

import java.util.Scanner;

/**
 * Wraps console prompts, validation, and typed input handling for the CLI.
 */
// performs input handling
public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);
    private final InputValidator validator = new InputValidator();

    /**
     * Reads a full line from the console after showing a prompt.
     */
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Reads a double value, re-prompting until valid.
     */
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


    /**
     * Reads an integer value, re-prompting until valid.
     */
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

    /**
     * Collects and validates an age value.
     */
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

    /**
     * Collects and validates a person name.
     */
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

    /**
     * Resolves an account by repeatedly asking for an account number.
     */
    public Account getAccount(String prompt, AccountManager acm) {
        String accountNumber;
        Account account;
        while (true) {
            accountNumber = getStringInput(prompt);
            try {
                 validator.validateAccountNumberFormat(accountNumber);
                 account = acm.findAccount(accountNumber);
                 return account;
            } catch (InvalidAccountException | InvalidAccountNumberException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Collects and validates a contact number.
     */
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

    /**
     * Prompts for an initial deposit, enforcing account-specific minimums.
     */
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

    /**
     * Captures the user's account type selection.
     */
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

    /**
     * Captures the user's customer type selection.
     */
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

    /**
     * Captures the user's transaction type selection.
     */
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

    /**
     * Prompts the operator to choose the transaction sort field.
     */
    public int getTransactionSortFieldChoice() {
        System.out.println("Select sort field:");
        System.out.println("1. Date/Time");
        System.out.println("2. Amount");
        System.out.println("3. Type");
        System.out.println("4. Balance After");
        while (true) {
            int choice = getIntInput("Enter choice (1-4): ", "Choice must be a number");
            try {
                validator.validateSortFieldChoice(choice);
                return choice;
            } catch (InvalidChoiceException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Prompts the operator to choose ascending or descending sort order.
     */
    public int getTransactionSortDirectionChoice() {
        System.out.println("Select sort order:");
        System.out.println("1. Ascending (oldest first for time-based sorting)");
        System.out.println("2. Descending (latest first for time-based sorting)");
        while (true) {
            int choice = getIntInput("Enter choice (1-2): ", "Choice must be a number");
            try {
                validator.validateSortDirectionChoice(choice);
                return choice;
            } catch (InvalidChoiceException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Reads and validates deposit amounts.
     */
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

    /**
     * Reads and validates withdrawal amounts against an account.
     */
    public double getWithdrawalAmount(String prompt, Account account) {
        double amount;
        while (true) {
            amount = getDoubleInput(prompt, "Amount must be a number");
            try{
                if(account.getAccountType().equalsIgnoreCase("savings")) validator.validateSavingsWithdrawal(amount, account.getBalance());
                else validator.validateCheckingWithdrawal(amount, account.getBalance());
                break; // Valid input, exit loop
            }
            catch(InsufficientFundsException | InvalidWithdrawalAmountException | OverdraftExceededException e){
                System.out.println(e.getMessage());
            }
        }
        return amount;
    }

    public String getEmail(String prompt) {
        String email;
        while (true) {
            email = getStringInput(prompt);
            try {
                validator.validateEmail(email);
                break; // Valid input, exit loop
            } catch (InvalidEmailException e) {
                System.out.println(e.getMessage());
            }
        }
        return email;
    }

    /**
     * Waits for the user to press enter, useful for paging.
     */
    public void waitForEnter() {
        scanner.nextLine();
    }

    /**
     * Closes the shared scanner resource.
     */
    public void closeScanner() {
        scanner.close();
    }

}
