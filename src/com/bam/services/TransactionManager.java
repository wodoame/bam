package com.bam.services;

import com.bam.models.Account;
import com.bam.models.CheckingAccount;
import com.bam.models.Transaction;

import java.util.Scanner;

public class TransactionManager {
    private final Transaction[] transactions;
    private int transactionCount;

    public TransactionManager() {
        this.transactions = new Transaction[200];
        this.transactionCount = 0;
    }

    public static double getBalanceAfter(Account account, double amount, String transactionType) {
        if (transactionType.equalsIgnoreCase("deposit") || transactionType.equalsIgnoreCase("transfer in")) {
            if (account.getAccountType().equalsIgnoreCase("Checking")) {
                // For checking accounts, include overdraft limit in balance calculation
                return account.getBalance() + CheckingAccount.OVERDRAFT_LIMIT + amount;
            }
            return account.getBalance() + amount; // For savings accounts
        } else if (transactionType.equalsIgnoreCase("withdrawal") || transactionType.equalsIgnoreCase("transfer out")) {
            if (account.getAccountType().equalsIgnoreCase("Checking")) {
                // For checking accounts, include overdraft limit in balance calculation
                return account.getBalance() + CheckingAccount.OVERDRAFT_LIMIT - amount;
            }
            return account.getBalance() - amount;
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
    }

    public void addTransaction(Transaction transaction) {
        if (transactionCount < transactions.length) {
            transactions[transactionCount++] = transaction;
        } else {
            System.out.println("Transaction history full. Cannot add new transaction.");
        }
    }

    public void viewTransactionsByAccount(String accountNumber) {
        System.out.println("TRANSACTION HISTORY FOR ACCOUNT NUMBER " + accountNumber);
        final String headerFormat = "%-10s | %-12s | %-12s | %-12s | %-25s%n";
        final String rowFormat = "%-10s | %-12s | %-12s | %-12s | %-25s%n";
        final int tableWidth = 10 + 3 + 12 + 3 + 12 + 3 + 12 + 3 + 25;
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "TXN ID", "TYPE", "AMOUNT", "BALANCE", "DATE/TIME");
        System.out.println(divider);
        boolean found = false;
        // Display in reverse chronological order (newest first)
        for (int i = transactionCount - 1; i >= 0; i--) {
            Transaction txn = transactions[i];
            if (txn.getAccountNumber().equals(accountNumber)) {
                String type = txn.getType().toUpperCase();
                String sign = txn.getType().equalsIgnoreCase("deposit") || txn.getType().equalsIgnoreCase("transfer in")
                        ? "+"
                        : "-";
                String amountValue = String.format("%s$%.2f", sign, txn.getAmount());
                String balanceValue = String.format("$%.2f", txn.getBalanceAfter());
                System.out.printf(
                        rowFormat,
                        txn.getTransactionId(),
                        type,
                        amountValue,
                        balanceValue,
                        txn.getTimestamp().toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No transactions found for this account.");
        }
        System.out.println(divider);

        // Display summary statistics
        if (found) {
            double totalDeposits = calculateTotalDeposits(accountNumber);
            double totalWithdrawals = calculateTotalWithdrawals(accountNumber);
            double totalTransfersIn = calculateTotalTransfersIn(accountNumber);
            double totalTransfersOut = calculateTotalTransfersOut(accountNumber);
            double netChange = (totalDeposits + totalTransfersIn) - (totalWithdrawals + totalTransfersOut);
            System.out.println("\nSUMMARY:");
            System.out.printf("Total Deposits:     +$%.2f%n", totalDeposits);
            System.out.printf("Total Withdrawals:  -$%.2f%n", totalWithdrawals);
            System.out.printf("Total Transfers In: +$%.2f%n", totalTransfersIn);
            System.out.printf("Total Transfers Out: -$%.2f%n", totalTransfersOut);
            System.out.printf("Net Change:         %s$%.2f%n", netChange >= 0 ? "+" : "", netChange);
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }

    public double calculateTotalDeposits(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber) &&
                    transactions[i].getType().equalsIgnoreCase("deposit")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    public double calculateTotalWithdrawals(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber) &&
                    transactions[i].getType().equalsIgnoreCase("withdrawal")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    public double calculateTotalTransfersIn(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber) &&
                    transactions[i].getType().equalsIgnoreCase("Transfer In")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    public double calculateTotalTransfersOut(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber) &&
                    transactions[i].getType().equalsIgnoreCase("Transfer Out")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void generateStatement(com.bam.models.Account account) {
        String accountNumber = account.getAccountNumber();
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ACCOUNT STATEMENT");
        System.out.println("=".repeat(70));
        System.out.println("\nACCOUNT INFORMATION:");
        System.out.println("-".repeat(70));
        System.out.printf("%-25s: %s%n", "Account Number", accountNumber);
        System.out.printf("%-25s: %s%n", "Account Holder", account.getCustomer().getName());
        System.out.printf("%-25s: %s%n", "Account Type", account.getAccountType());
        System.out.printf("%-25s: $%.2f%n", "Current Balance", account.getBalance());
        System.out.printf("%-25s: %s%n", "Account Status", "Active");

        System.out.println("\nTransactions:");
        final String headerFormat = "%-10s | %-12s | %-12s | %-12s | %-25s%n";
        final String rowFormat = "%-10s | %-12s | %-12s | %-12s | %-25s%n";
        final int tableWidth = 10 + 3 + 12 + 3 + 12 + 3 + 12 + 3 + 25;
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "TXN ID", "TYPE", "AMOUNT", "BALANCE", "DATE/TIME");
        System.out.println(divider);

        boolean found = false;
        // Display in reverse chronological order (newest first)
        for (int i = transactionCount - 1; i >= 0; i--) {
            Transaction txn = transactions[i];
            if (txn.getAccountNumber().equals(accountNumber)) {
                String type = txn.getType().toUpperCase();
                String sign = txn.getType().equalsIgnoreCase("deposit") ? "+" : "-";
                String amountValue = String.format("%s$%.2f", sign, txn.getAmount());
                String balanceValue = String.format("$%.2f", txn.getBalanceAfter());
                System.out.printf(
                        rowFormat,
                        txn.getTransactionId(),
                        type,
                        amountValue,
                        balanceValue,
                        txn.getTimestamp().toString());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found for this account.");
        }
        System.out.println(divider);

        // Display summary statistics
        // System.out.println("\nSTATEMENT SUMMARY:");
        // System.out.println("-".repeat(70));
        double totalDeposits = calculateTotalDeposits(accountNumber);
        double totalWithdrawals = calculateTotalWithdrawals(accountNumber);
        double totalTransfersIn = calculateTotalTransfersIn(accountNumber);
        double totalTransfersOut = calculateTotalTransfersOut(accountNumber);
        double netChange = (totalDeposits + totalTransfersIn) - (totalWithdrawals + totalTransfersOut);
        // System.out.printf("%-25s: +$%.2f%n", "Total Deposits", totalDeposits);
        // System.out.printf("%-25s: -$%.2f%n", "Total Withdrawals", totalWithdrawals);
        System.out.printf("%-25s: %s$%.2f%n", "Net Change", netChange >= 0 ? "+" : "", netChange);
        System.out.println("\n✓ Statement generated successfully.");
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }
}
