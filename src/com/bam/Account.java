package com.bam;

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

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            // System.out.println("Deposit successful. New Balance: $" + balance); // Logic separation: Print in main or return status? Instructions imply void.
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
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
            deposit(amount);
            return true;
        } else if (type.equalsIgnoreCase("withdrawal")) {
            return withdraw(amount);
        }
        return false;
    }
}
