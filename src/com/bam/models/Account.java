package com.bam.models;

import com.bam.exceptions.InvalidDepositAmountException;
import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.InsufficientFundsException;
import com.bam.exceptions.OverdraftExceededException;
import com.bam.exceptions.InvalidAccountException;
import com.bam.interfaces.Transactable;
import com.bam.utils.InputValidator;

public abstract class Account implements Transactable {
    protected String accountNumber;
    protected Customer customer;
    protected double balance;
    protected String status;
    protected static int accountCounter = 1;

    public Account(Customer customer, double initialDeposit) {
        this.customer = customer;
        this.balance = initialDeposit;
        this.status = "Active";
        this.accountNumber = generateAccountNumber();
    }

    private String generateAccountNumber() {
        return String.format("ACC%03d", accountCounter++);
    }

    public abstract void displayAccountDetails();

    public abstract String getAccountType();

    public boolean deposit(double amount) {
        InputValidator validator = new InputValidator();
        validator.validateDepositAmount(amount);
        balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        InputValidator validator = new InputValidator();
        validator.validateWithdrawalAmount(amount, this);
        balance -= amount;
        return true;
    };

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

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

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
}