package com.bam.models;

import com.bam.exceptions.InvalidDepositAmountException;
import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.InsufficientFundsException;
import com.bam.exceptions.OverdraftExceededException;
import com.bam.exceptions.InvalidAccountException;
import com.bam.interfaces.Transactable;
import com.bam.utils.InputValidator;

/**
 * Base contract for all bank accounts, encapsulating shared identity,
 * balance, and transaction-processing behaviors.
 */
public abstract class Account implements Transactable {
    protected String accountNumber;
    protected Customer customer;
    protected double balance;
    protected String status;
    protected static int accountCounter = 1;

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
    public boolean deposit(double amount) {
        InputValidator validator = new InputValidator();
        validator.validateDepositAmount(amount);
        balance += amount;
        return true;
    }

    /**
     * Validates and withdraws funds from the balance.
     *
     * @param amount amount to subtract
     * @return {@code true} when the withdrawal succeeds
     */
    public boolean withdraw(double amount) {
        InputValidator validator = new InputValidator();
        validator.validateWithdrawalAmount(amount, this);
        balance -= amount;
        return true;
    };

    /**
     * Transfers funds to the target account after validating both accounts.
     *
     * @param targetAccount destination account
     * @param amount        amount to move
     */
    public void transfer(Account targetAccount, double amount) {
        if (targetAccount == null) {
            throw new InvalidAccountException("Target account cannot be null");
        }
        if (this == targetAccount) {
            throw new InvalidAccountException("Cannot transfer to the same account");
        }

        // Withdraw from this account first
        // If this fails (e.g. insufficiency funds), it will throw an exception
        // and the transfer will be aborted, which is what we want.
        this.withdraw(amount);

        // Then deposit to target account
        targetAccount.deposit(amount);
        System.out.printf("Transferred $%.2f to %s\n", amount, targetAccount.getAccountNumber());
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
        return balance;
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
            return deposit(amount);
        }
        if (type.equalsIgnoreCase("withdrawal")) {
            try {
                boolean res = withdraw(amount);
                System.out.println("Withdrawal successful.");
                return res;
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
            } catch (InvalidAccountException | InsufficientFundsException | InvalidWithdrawalAmountException
                    | OverdraftExceededException e) {
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