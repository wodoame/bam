package com.bam.models;

import com.bam.exceptions.InvalidDepositAmountException;
import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.InsufficientFundsException;
import com.bam.exceptions.OverdraftExceededException;
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

    public boolean withdraw(double amount){
        InputValidator validator = new InputValidator();
        validator.validateWithdrawalAmount(amount, this);
        balance -= amount;
        return true;
    };

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
            try{
                boolean res = withdraw(amount);
                System.out.println("Withdrawal successful.");
                return res;
            }catch(InsufficientFundsException | InvalidWithdrawalAmountException | OverdraftExceededException e){
                System.out.println(e.getMessage());
            }
        }
        return false;
    }
}