package com.bam.models;

import com.bam.services.TransactionManager;

import java.util.ArrayList;
import java.util.Date;

public class Transaction {
    private String transactionId;
    private final String accountNumber;
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final Date timestamp;
    private boolean idGenerated = false;

    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = new Date();
        this.transactionId = null;
    }

    // Called after a transaction is added
    public void generateTransactionId() {
        if (!idGenerated) {
            ArrayList<Transaction> accountTransactions = (ArrayList<Transaction>) TransactionManager.getTransactions(accountNumber);
            int count = accountTransactions.size();
            transactionId = String.format("TXN%03d", count);
            idGenerated = true; // making the id generation idempotent
        }
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void displayTransactionDetails() {
        System.out.println(transactionId == null ? "" : "Transaction ID: " + transactionId);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Type: " + type);
        System.out.printf("Amount: $%.2f%n", amount);
        System.out.printf("Previous Balance: $%.2f%n", type.equalsIgnoreCase("Deposit") ? balanceAfter - amount : balanceAfter + amount);
        System.out.printf("New Balance: $%.2f%n", balanceAfter);
        System.out.println("Timestamp: " + timestamp);
    }
}
