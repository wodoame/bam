package com.bam.models;

import com.bam.exceptions.*;
import com.bam.interfaces.Transactable;
import com.bam.utils.InputValidator;

/**
 * Base contract for all bank accounts, encapsulating shared identity,
 * balance, and transaction-processing behaviors.
 */
public abstract class Account implements Transactable {
    protected String accountNumber;
    protected Customer customer;
    protected volatile double balance;
    protected String status;
    protected static int accountCounter = 1;
    private final Object balanceLock = new Object();

    /**
     * Creates a new account with a freshly generated account number.
     *
     * @param customer        owner of the account
     * @param initialDeposit  opening deposit amount
     */
    public Account(Customer customer, double initialDeposit) {
        this(customer, initialDeposit, null, "Active", true);
    }

    /**
     * Rehydrates an existing account using persisted fields.
     */
    protected Account(Customer customer, double balance, String accountNumber, String status) {
        this(customer, balance, accountNumber, status, false);
    }

    private Account(Customer customer, double balance, String accountNumber, String status, boolean autoGenerateNumber) {
        this.customer = customer;
        this.balance = balance;
        this.status = status == null ? "Active" : status;
        if (autoGenerateNumber) {
            this.accountNumber = generateAccountNumber();
        } else {
            if (accountNumber == null || accountNumber.isBlank()) {
                throw new IllegalArgumentException("Account number must be provided for persisted accounts.");
            }
            this.accountNumber = accountNumber;
        }
    }

    /**
     * Assigns the next account number in sequence.
     */
    private String generateAccountNumber() {
        return String.format("ACC%03d", accountCounter++);
    }

    /**
     * Displays account-specific attributes for CLI output.
     */
    public abstract void displayAccountDetails();

    /**
     * @return human-readable account type label (e.g., "Savings").
     */
    public abstract String getAccountType();

    /**
     * Validates and applies a deposit to the current balance.
     *
     * @param amount amount to add
     * @return {@code true} when the deposit succeeds
     */
    public boolean deposit(double amount) throws InvalidDepositAmountException {
        InputValidator validator = new InputValidator();
        validator.validateDepositAmount(amount);
        synchronized (balanceLock) {
            balance += amount;
            return true;
        }
    }

    /**
     * Validates and withdraws funds from the balance.
     *
     * @param amount amount to subtract
     * @return {@code true} when the withdrawal succeeds
     */
    public abstract boolean withdraw(double amount) throws InsufficientFundsException, OverdraftExceededException, InvalidWithdrawalAmountException;

    /**
     * Transfers funds to the target account after validating both accounts.
     *
     * @param targetAccount destination account
     * @param amount        amount to move
     */
    public void transfer(Account targetAccount, double amount) throws InsufficientFundsException, OverdraftExceededException, InvalidWithdrawalAmountException, InvalidDepositAmountException {
        if (targetAccount == null) {
            throw new InvalidAccountException("Target account cannot be null");
        }
        if (this == targetAccount) {
            throw new InvalidAccountException("Cannot transfer to the same account");
        }
        Object firstLock = this.balanceLock;
        Object secondLock = targetAccount.balanceLock;
        if (System.identityHashCode(firstLock) > System.identityHashCode(secondLock)) {
            Object temp = firstLock;
            firstLock = secondLock;
            secondLock = temp;
        }
        synchronized (firstLock) {
            synchronized (secondLock) {
                this.withdraw(amount);
                targetAccount.deposit(amount);
                System.out.printf("Transferred $%.2f to %s\n", amount, targetAccount.getAccountNumber());
            }
        }
    }

    /** @return unique account number. */
    public String getAccountNumber() {
        return accountNumber;
    }

    /** @return owning customer. */
    public Customer getCustomer() {
        return customer;
    }

    /** @return current cash balance. */
    public double getBalance() {
        synchronized (balanceLock) {
            return balance;
        }
    }

    /** @return lifecycle status string. */
    public String getStatus() {
        return status;
    }

    /**
     * Handles deposit/withdrawal flows for the CLI transaction engine.
     */
    @Override
    public boolean processTransaction(double amount, String type) {
        if (type.equalsIgnoreCase("deposit")) {
            try {
                return deposit(amount);
            } catch (InvalidDepositAmountException e) {
                System.out.println(e.getMessage());
            }
        }
        if (type.equalsIgnoreCase("withdrawal")) {
            try {
                return withdraw(amount);
            } catch (InsufficientFundsException | InvalidWithdrawalAmountException | OverdraftExceededException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Handles transfer flows that involve two accounts.
     */
    public boolean processTransaction(double amount, String type, Account targetAccount) {
        if (type.equalsIgnoreCase("transfer")) {
            try {
                transfer(targetAccount, amount);
                return true;
            } catch (InvalidAccountException | InsufficientFundsException | InvalidWithdrawalAmountException |
                     OverdraftExceededException | InvalidDepositAmountException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Updates the static counter so new accounts continue after persisted ones.
     */
    public static void setAccountCounter(int nextCounter) {
        if (nextCounter > 0) {
            accountCounter = nextCounter;
        }
    }
}