package com.bam.services;

import com.bam.models.Account;
import com.bam.models.CheckingAccount;
import com.bam.models.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionManager {
    private  static final Map<String, List<Transaction>> transactionsMap = new HashMap<>();

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
        transactionsMap
                .computeIfAbsent(transaction.getAccountNumber(), key -> new ArrayList<>())
                .add(transaction);
        transaction.generateTransactionId(); // IMPORTANT
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
        List<Transaction> accountTransactions = getTransactions(accountNumber);
        accountTransactions.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .forEach(txn -> {
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
                });
        if (accountTransactions.isEmpty()) {
            System.out.println("No transactions found for this account.");
        }
        System.out.println(divider);

        // Display summary statistics
        if (!accountTransactions.isEmpty()) {
            printSummary(accountNumber);
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }


    public double calculateTotalTransaction(String accountNumber, String type ) {
        return getTransactions(accountNumber).stream()
                .filter(txn -> txn.getType().equalsIgnoreCase(type))
                .mapToDouble(Transaction::getAmount)
                .sum();
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

        List<Transaction> accountTransactions = getTransactions(accountNumber);
        accountTransactions.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .forEach(txn -> {
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
                });

        if (accountTransactions.isEmpty()) {
            System.out.println("No transactions found for this account.");
        }
        System.out.println(divider);

        // Display summary statistics
        printSummary(accountNumber);
        System.out.println("\n✓ Statement generated successfully.");
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    public static List<Transaction> getTransactions(String accountNumber) {
        return transactionsMap.getOrDefault(accountNumber, List.of());
    }

    public List<Transaction> getTransactionsMatching(String accountNumber, Predicate<Transaction> predicate) {
        return getTransactions(accountNumber).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public Optional<Transaction> getMostRecentTransaction(String accountNumber) {
        return getTransactions(accountNumber).stream()
                .max(Comparator.comparing(Transaction::getTimestamp));
    }

    private void printSummary(String accountNumber) {
        double totalDeposits = calculateTotalTransaction(accountNumber, "deposit");
        double totalWithdrawals = calculateTotalTransaction(accountNumber, "withdrawal");
        double totalTransfersIn = calculateTotalTransaction(accountNumber, "transfer in");
        double totalTransfersOut = calculateTotalTransaction(accountNumber, "transfer out");
        double netChange = (totalDeposits + totalTransfersIn) - (totalWithdrawals + totalTransfersOut);
        System.out.println("\nSUMMARY:");
        System.out.printf("Total Deposits:     +$%.2f%n", totalDeposits);
        System.out.printf("Total Withdrawals:  -$%.2f%n", totalWithdrawals);
        System.out.printf("Total Transfers In: +$%.2f%n", totalTransfersIn);
        System.out.printf("Total Transfers Out: -$%.2f%n", totalTransfersOut);
        System.out.printf("Net Change:         %s$%.2f%n", netChange >= 0 ? "+" : "", netChange);
    }
}
