package com.bam;

import java.util.Date;

public class Transaction {
    private final String transactionId;
    private final String accountNumber;
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final Date timestamp;
    private static int transactionCounter = 1;

    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = new Date();
        this.transactionId = generateTransactionId();
    }

    private String generateTransactionId() {
        return String.format("TXN%03d", transactionCounter++);
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
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Type: " + type);
        System.out.printf("Amount: $%.2f%n", amount);
        System.out.printf("Previous Balance: $%.2f%n", type.equalsIgnoreCase("Deposit")? balanceAfter - amount: balanceAfter + amount);
        System.out.printf("New Balance: $%.2f%n", balanceAfter);
        System.out.println("Timestamp: " + timestamp);
    }
}
