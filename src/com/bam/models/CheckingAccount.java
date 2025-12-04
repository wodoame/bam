package com.bam;

public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double monthlyFee;

    public CheckingAccount(Customer customer, double initialDeposit) {
        super(customer, initialDeposit);
        this.overdraftLimit = 1000.0;
        this.monthlyFee = 10.0;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Account ID: " + accountNumber);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Type: " + getAccountType());
        System.out.println("Balance: $" + balance);
        System.out.println("Overdraft Limit: $" + overdraftLimit);
        System.out.println("Monthly Fee: $" + monthlyFee);
        System.out.println("Status: " + status);
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && (balance - amount) >= -overdraftLimit) {
            balance -= amount;
            System.out.println("Withdrawal successful. New Balance: $" + balance);
            return true;
        } else {
            System.out.println("Withdrawal failed. Exceeds overdraft limit.");
            return false;
        }
    }

    public void applyMonthlyFee() {
        // Check if customer is premium to waive fee?
        // Instructions say "Premium customers have waived monthly fees".
        // So we should check the customer type.
        if (customer instanceof PremiumCustomer && ((PremiumCustomer) customer).hasWaivedFees()) {
            System.out.println("Monthly fee waived for Premium customer.");
        } else {
            balance -= monthlyFee;
            System.out.println("Monthly fee applied: $" + monthlyFee);
        }
    }
}
