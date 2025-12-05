package com.bam.models;

import com.bam.exceptions.InvalidDepositAmountException;
import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.InsufficientFundsException;
import com.bam.interfaces.Transactable;

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
        if (amount <= 0) {
            throw new InvalidDepositAmountException("Deposit amount must be greater than zero.");
        }
        balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidWithdrawalAmountException("Withdrawal amount must be greater than zero.");
        }
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: $" + balance);
        }
        balance -= amount;
        return true;
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
            try {
               return deposit(amount);
            }catch(InvalidDepositAmountException e){
                System.out.println(e.getMessage());
            }
        } else if (type.equalsIgnoreCase("withdrawal")) {
            try{
                return withdraw(amount);
            }catch(InvalidWithdrawalAmountException | InsufficientFundsException e){
                System.out.println(e.getMessage());
            }
        }
        return false;
    }
}