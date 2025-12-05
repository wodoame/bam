package com.bam.models;

import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.InsufficientFundsException;

public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;
    public static final double MINIMUM_BALANCE = 500.0;

    public SavingsAccount(Customer customer, double initialDeposit) {
        super(customer, initialDeposit);
        this.interestRate = 3.5;
        this.minimumBalance = MINIMUM_BALANCE;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Account ID: " + accountNumber);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Type: " + getAccountType());
        System.out.println("Balance: $" + balance);
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Minimum Balance: $" + minimumBalance);
        System.out.println("Status: " + status);
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidWithdrawalAmountException("Withdrawal amount must be greater than zero.");
        }
        if ((balance - amount) < minimumBalance) {
            // Use a different exception here
            throw new InsufficientFundsException("Withdrawal failed. Insufficient funds or minimum balance constraint. You must have at least $" + minimumBalance + " remaining after withdrawal.");
        }
        balance -= amount;
        System.out.println("Withdrawal successful. New Balance: $" + balance);
        return true;
    }

    public void calculateInterest() {
        double interest = balance * (interestRate / 100);
        deposit(interest);
        System.out.println("Interest calculated and added: $" + interest);
    }
}
