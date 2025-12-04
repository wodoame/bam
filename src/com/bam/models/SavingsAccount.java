package com.bam;

public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;

    public SavingsAccount(Customer customer, double initialDeposit) {
        super(customer, initialDeposit);
        this.interestRate = 3.5;
        this.minimumBalance = 500.0;
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
        if (amount > 0 && (balance - amount) >= minimumBalance) {
            balance -= amount;
            System.out.println("Withdrawal successful. New Balance: $" + balance);
            return true;
        } else {
            System.out.println("Withdrawal failed. Insufficient funds or minimum balance constraint.");
            System.out.println("You must have at least $" + minimumBalance + " remaining after withdrawal.");
            return false;
        }
    }

    public void calculateInterest() {
        double interest = balance * (interestRate / 100);
        deposit(interest);
        System.out.println("Interest calculated and added: $" + interest);
    }
}
