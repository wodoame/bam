package com.bam;

import java.util.Scanner;

public class TransactionManager {
    private final Transaction[] transactions;
    private int transactionCount;

    public TransactionManager() {
        this.transactions = new Transaction[200];
        this.transactionCount = 0;
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
        final String headerFormat = "%-10s | %-12s | %-12s | %-25s%n";
        final String rowFormat = "%-10s | %-12s | %-12s | %-25s%n";
        final int tableWidth = 10 + 3 + 12 + 3 + 12 + 3 + 25;
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "TXN ID", "AMOUNT", "BALANCE", "DATE/TIME");
        System.out.println(divider);
        boolean found = false;
        int totalTransactions = 0;
        double totalDeposits = 0;
        double totalWithdrawals = 0;
        // Display in reverse chronological order (newest first)
        for (int i = transactionCount - 1; i >= 0; i--) {
            Transaction txn = transactions[i];
            if (txn.getAccountNumber().equals(accountNumber)) {
                String sign = txn.getType().equalsIgnoreCase("deposit") ? "+" : "-";
                String amountValue = String.format("%s$%.2f", sign, txn.getAmount());
                String balanceValue = String.format("$%.2f", txn.getBalanceAfter());
                System.out.printf(
                        rowFormat,
                        txn.getTransactionId(),
                        amountValue,
                        balanceValue,
                        txn.getTimestamp().toString()
                );
                found = true;
                totalTransactions++;
                if (txn.getType().equalsIgnoreCase("deposit")) {
                    totalDeposits += txn.getAmount();
                } else {
                    totalWithdrawals += txn.getAmount();
                }
            }
        }
        if (!found) {
            System.out.println("No transactions found for this account.");
        }
        System.out.println(divider);

        // Display summary statistics
        if (found) {
            double netChange = totalDeposits - totalWithdrawals;
            System.out.println("\nSUMMARY:");
            System.out.printf("Total Transactions: %d%n", totalTransactions);
            System.out.printf("Total Deposits:     +$%.2f%n", totalDeposits);
            System.out.printf("Total Withdrawals:  -$%.2f%n", totalWithdrawals);
            System.out.printf("Net Change:         %s$%.2f%n", netChange >= 0 ? "+" : "", netChange);
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }

    public double calculateTotalDeposits() {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getType().equalsIgnoreCase("deposit")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    public double calculateTotalWithdrawals() {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getType().equalsIgnoreCase("withdrawal")) {
                total += transactions[i].getAmount();
            }
        }
        return total;
    }

    public int getTransactionCount() {
        return transactionCount;
    }
}
