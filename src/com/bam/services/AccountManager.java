package com.bam.services;

import com.bam.exceptions.InvalidAccountException;
import com.bam.models.Account;
import com.bam.models.RegularCustomer;
import com.bam.models.SavingsAccount;
import com.bam.models.Transaction;

import java.util.Scanner;

public class AccountManager {
    private final Account[] accounts;
    private int accountCount;

    public AccountManager() {
        this.accounts = new Account[50];
        this.accountCount = 0;
    }

    // By default, this method should show success message and the customer details
    public void addAccount(Account account) {
        addAccount(account, false);
    }

    public void addAccount(Account account, boolean silent) {
        if (accountCount < accounts.length) {
            accounts[accountCount++] = account;
            if (!silent) {
                System.out.println("\nAccount created successfully!");
                account.displayAccountDetails();
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();
            }
        } else {
            System.out.println("Account limit reached. Cannot create new account.");
        }
    }

    public Account findAccount(String accountNumber) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        throw new InvalidAccountException("Account number " + accountNumber + " not found.");
    }

    public void viewAllAccounts() {
        System.out.println("ACCOUNT LISTING");
        final String headerFormat = "%-10s | %-20s | %-10s | %-12s | %-10s%n";
        final String rowFormat = "%-10s | %-20s | %-10s | %-12s | %-10s%n";
        final int tableWidth = 10 + 3 + 20 + 3 + 10 + 3 + 12 + 3 + 10; // column widths plus separator spacing
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "ACC NO", "CUSTOMER NAME", "TYPE", "BALANCE", "STATUS");
        System.out.println(divider);
        for (int i = 0; i < accountCount; i++) {
            Account account = accounts[i];
            String balanceValue = String.format("$%.2f", account.getBalance());
            System.out.printf(
                    rowFormat,
                    account.getAccountNumber(),
                    account.getCustomer().getName(),
                    account.getAccountType(),
                    balanceValue,
                    account.getStatus()
            );
        }
        System.out.println(divider);
        System.out.println("Total Accounts: " + accountCount);
        System.out.printf("Total Bank Balance: $%.2f%n", getTotalBalance());
    }

    public double getTotalBalance() {
        double total = 0;
        for (int i = 0; i < accountCount; i++) {
            total += accounts[i].getBalance();
        }
        return total;
    }

    public void generateSeedAccounts(TransactionManager transactionManager) {
        String[] names = {"Bernard", "Alice", "John", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack"};
        for (String name: names){
            double initialDeposit = 500;
            RegularCustomer customer = new RegularCustomer(name, 30, "0123456789", "Accra, Ghana");
            SavingsAccount account = new SavingsAccount(customer, initialDeposit);
            addAccount(account, true);
            Transaction txn = new Transaction(account.getAccountNumber(), "Deposit", initialDeposit, account.getBalance());
            transactionManager.addTransaction(txn);
        }
    }

    public int getAccountCount() {
        return accountCount;
    }
}
