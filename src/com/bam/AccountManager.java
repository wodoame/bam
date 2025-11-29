package com.bam;

import java.util.Scanner;

public class AccountManager {
    private final Account[] accounts;
    private int accountCount;

    public AccountManager() {
        this.accounts = new Account[50];
        this.accountCount = 0;
    }

    public void addAccount(Account account) {
        if (accountCount < accounts.length) {
            accounts[accountCount++] = account;
            System.out.println("\nAccount created successfully!");
            account.displayAccountDetails();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
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
        return null;
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

    public int getAccountCount() {
        return accountCount;
    }
}
