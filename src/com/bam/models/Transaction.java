package com.bam.models;

import com.bam.services.TransactionManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Immutable view of a ledger entry tying an amount, type, and timestamp to an account.
 */
public class Transaction {
    private String transactionId;
    private final String accountNumber;
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private Date timestamp;
    private boolean idGenerated = false;

    /**
     * Creates a runtime transaction that will generate its ID upon persistence.
     */
    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        this(null, accountNumber, type, amount, balanceAfter, new Date());
        this.idGenerated = false;
    }

    /**
     * Rehydrates a transaction from storage using the provided metadata.
     */
    public Transaction(String transactionId, String accountNumber, String type, double amount, double balanceAfter, Date timestamp) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
        this.idGenerated = transactionId != null;
    }

    /**
     * Generates a sequential transaction ID for the owning account when needed.
     */
    // Called after a transaction is added
    public void generateTransactionId() {
        if (!idGenerated) {
            java.util.List<Transaction> accountTransactions = TransactionManager.getTransactions(accountNumber);
            int count = accountTransactions.size();
            transactionId = String.format("TXN%03d", count);
            idGenerated = true; // making the id generation idempotent
        }
    }

    /** @return identifier (may be null prior to persistence). */
    public String getTransactionId() {
        return transactionId;
    }

    /** @return transaction timestamp. */
    public Date getTimestamp() {
        return timestamp;
    }

    /** @return account number the transaction applies to. */
    public String getAccountNumber() {
        return accountNumber;
    }

    /** @return transaction type label. */
    public String getType() {
        return type;
    }

    /** @return signed amount applied during the transaction. */
    public double getAmount() {
        return amount;
    }

    /** @return resulting account balance after the transaction. */
    public double getBalanceAfter() {
        return balanceAfter;
    }

    /**
     * Allows explicitly setting a transaction ID when reloading persisted entries.
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        this.idGenerated = transactionId != null;
    }

    /**
     * Updates the timestamp for reloaded entries.
     */
    public void setTimestamp(Date timestamp) {
        if (timestamp != null) {
            this.timestamp = timestamp;
        }
    }

    /**
     * Prints a human-readable breakdown of the transaction for CLI confirmation dialogs.
     */
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
