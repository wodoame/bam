package com.bam;

import java.util.Scanner;

public class Main {
    private static final AccountManager accountManager = new AccountManager();
    private static final TransactionManager transactionManager = new TransactionManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    accountManager.viewAllAccounts();
                    break;
                case 3:
                    processTransaction();
                    break;
                case 4:
                    viewTransactionHistory();
                    break;
                case 5:
                    exit = true;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Bank Account Management System ===");
        System.out.println("1. Create Account");
        System.out.println("2. View All Accounts");
        System.out.println("3. Process Transaction");
        System.out.println("4. View Transaction History");
        System.out.println("5. Exit");
    }

    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.println("Select Customer Type:");
        System.out.println("1. Regular");
        System.out.println("2. Premium");
        int customerTypeChoice = getIntInput("Enter choice: ");

        String name = getStringInput("Enter Customer Name: ");
        int age = getIntInput("Enter Age: ");
        String contact = getStringInput("Enter Contact Number: ");
        String address = getStringInput("Enter Address: ");

        Customer customer;
        if (customerTypeChoice == 1) {
            customer = new RegularCustomer(name, age, contact, address);
        } else if (customerTypeChoice == 2) {
            customer = new PremiumCustomer(name, age, contact, address);
        } else {
            System.out.println("Invalid customer type selected.");
            return;
        }

        System.out.println("Select Account Type:");
        System.out.println("1. Savings");
        System.out.println("2. Checking");
        int accountTypeChoice = getIntInput("Enter choice: ");

        double initialDeposit = getDoubleInput("Enter Initial Deposit: ");

        Account account = null;
        if (accountTypeChoice == 1) {
            if (initialDeposit < 500) {
                System.out.println("Minimum balance for Savings Account is $500.");
                return;
            }
            account = new SavingsAccount(customer, initialDeposit);
        } else if (accountTypeChoice == 2) {
            account = new CheckingAccount(customer, initialDeposit);
        } else {
            System.out.println("Invalid account type selected.");
            return;
        }

        accountManager.addAccount(account);
        // Initial deposit transaction
        Transaction txn = new Transaction(account.getAccountNumber(), "Deposit", initialDeposit, account.getBalance());
        transactionManager.addTransaction(txn);
    }

    private static boolean showTransactionConfirmationPrompt(Transaction txn) {
        System.out.println("\nTRANSACTION CONFIRMATION");
        System.out.println("________________________________");
        txn.displayTransactionDetails();
        String confirmationChoice = getStringInput("Confirm transaction? (Y/N): ");
        if (!confirmationChoice.equalsIgnoreCase("Y")) {
            System.out.println("Transaction cancelled.");
            return false;
        }
        return true;
    }
    private static void processTransaction() {
        System.out.println("\n--- Process Transaction ---");
        String accountNumber = getStringInput("Enter Account Number: ");
        Account account = accountManager.findAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("Select Transaction Type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        int typeChoice = getIntInput("Enter choice: ");

        double amount = getDoubleInput("Enter Amount: ");
        Transaction txn;

        boolean success = false;
        String type = "";
        if (typeChoice == 1) {
            type = "Deposit";
            txn = new Transaction(account.getAccountNumber(), type, amount, account.getBalance() + amount);
            boolean isConfirmed = showTransactionConfirmationPrompt(txn);
            if (!isConfirmed) {
                return;
            }
            account.deposit(amount);
            success = !(amount <= 0);
        } else if (typeChoice == 2) {
            type = "Withdrawal";
            txn = new Transaction(account.getAccountNumber(), type, amount, account.getBalance() - amount);
            boolean isConfirmed = showTransactionConfirmationPrompt(txn);
            if(!isConfirmed){
                return;
            }
            success = account.withdraw(amount);
        } else {
            System.out.println("Invalid transaction type.");
            return;
        }

        if (success) {
            transactionManager.addTransaction(txn);
            System.out.println("Transaction recorded.");
        }
    }

    private static void viewTransactionHistory() {
        String accountNumber = getStringInput("Enter Account Number: ");
        Account account = accountManager.findAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        transactionManager.viewTransactionsByAccount(accountNumber);
    }

    // Helper methods for input
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next(); // consume bad input
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return input;
    }

    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next(); // consume bad input
        }
        double input = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return input;
    }
}
