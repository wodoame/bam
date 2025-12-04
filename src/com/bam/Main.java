package com.bam;

import com.bam.models.*;
import com.bam.services.AccountManager;
import com.bam.services.TransactionManager;
import com.bam.utils.InputHandler;

import java.util.Scanner;

public class Main {
    private static final InputHandler inputHandler = new InputHandler();
    private static final AccountManager accountManager = new AccountManager();
    private static final TransactionManager transactionManager = new TransactionManager();

    public static void main(String[] args) {
        accountManager.generateSeedAccounts();
        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = inputHandler.getIntInput("Enter your choice: ", "Choice must be a number");
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
        inputHandler.closeScanner();
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
        int customerTypeChoice = inputHandler.getIntInput("Enter choice: ", "Choice must be a number");

        String name = inputHandler.getName("Enter Customer Name: ");
        int age = inputHandler.getAge("Enter Age: ");
        String contact = inputHandler.getStringInput("Enter Contact Number: ");
        String address = inputHandler.getStringInput("Enter Address: ");

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
        System.out.println("1. Savings (Interest Rate: 3.5, Minimum Balance: $500)");
        System.out.println("2. Checking (Overdraft: $1000, Monthly Fee: $10)");
        int accountTypeChoice = inputHandler.getIntInput("Enter choice: ", "Choice must be a number");

        double initialDeposit = inputHandler.getDoubleInput("Enter Initial Deposit: ", "Deposit must be a number");

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
        String confirmationChoice = inputHandler.getStringInput("Confirm transaction? (Y/N): ");
        if (!confirmationChoice.equalsIgnoreCase("Y")) {
            System.out.println("Transaction cancelled.");
            return false;
        }
        return true;
    }
    private static void processTransaction() {
        System.out.println("\n--- Process Transaction ---");
        Account account = inputHandler.getAccount("Enter Account Number: ", accountManager);
        System.out.println("Select Transaction Type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        int typeChoice = inputHandler.getIntInput("Enter choice: ", "Choice must be a number");
        double amount = inputHandler.getDoubleInput("Enter Amount: " , "Amount must be a number");
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
            if(amount > 0){
                account.processTransaction(amount, type);
            }else{
                System.out.println("Deposit amount must be greater than zero.");
            }
        } else if (typeChoice == 2) {
            type = "Withdrawal";
            txn = new Transaction(account.getAccountNumber(), type, amount, account.getBalance() - amount);
            boolean isConfirmed = showTransactionConfirmationPrompt(txn);
            if(!isConfirmed){
                return;
            }
            success = account.processTransaction(amount, type);
        } else {
            System.out.println("Invalid transaction type.");
            return;
        }

        if (success) {
            transactionManager.addTransaction(txn);
            System.out.println("Transaction recorded.");
        }
        System.out.println("Press Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    private static void viewTransactionHistory() {
        String accountNumber = inputHandler.getStringInput("Enter Account Number: ");
        Account account = accountManager.findAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        transactionManager.viewTransactionsByAccount(accountNumber);
    }
}
