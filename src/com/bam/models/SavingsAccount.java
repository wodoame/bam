package com.bam.models;

import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.InsufficientFundsException;
import com.bam.utils.InputValidator;

import javax.xml.validation.Validator;

/**
 * Savings account variant enforcing minimum balance and interest accrual.
 */
public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;
    public static final double MINIMUM_BALANCE = 500.0;
    public static final double INTEREST_RATE = 3.5;

    /**
     * Creates a new savings account with the default interest rate and minimum balance.
     */
    public SavingsAccount(Customer customer, double initialDeposit) {
        super(customer, initialDeposit);
        this.interestRate = INTEREST_RATE;
        this.minimumBalance = MINIMUM_BALANCE;
    }

    /**
     * Rehydrates a persisted savings account instance.
     */
    public SavingsAccount(Customer customer, double balance, String accountNumber, String status) {
        super(customer, balance, accountNumber, status);
        this.interestRate = INTEREST_RATE;
        this.minimumBalance = MINIMUM_BALANCE;
    }

    /** {@inheritDoc} */
    @Override
    public void displayAccountDetails() {
        System.out.println("Account ID: " + accountNumber);
        System.out.printf("Customer: %s (%s)\n", customer.getName(), customer.getCustomerType());
        System.out.println("Type: " + getAccountType());
        System.out.println("Balance: $" + balance);
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Minimum Balance: $" + minimumBalance);
        System.out.println("Status: " + status);
    }

    /** {@inheritDoc} */
    @Override
    public String getAccountType() {
        return "Savings";
    }

    /**
     * Applies interest to the balance using the configured rate.
     */
    public void calculateInterest() {
        double interest = balance * (interestRate / 100);
        deposit(interest);
        System.out.println("Interest calculated and added: $" + interest);
    }

    /** @return annual interest rate percentage. */
    public double getInterestRate() {
        return interestRate;
    }

    /** @return required minimum balance. */
    public double getMinimumBalance() {
        return minimumBalance;
    }
}
