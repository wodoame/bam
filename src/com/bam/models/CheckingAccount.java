package com.bam.models;


public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double monthlyFee;
    public static final double OVERDRAFT_LIMIT = 1000.0;
    public static final double MONTHLY_FEE = 10.0;

    public CheckingAccount(Customer customer, double initialDeposit) {
        super(customer, initialDeposit);
        this.overdraftLimit = OVERDRAFT_LIMIT;
        this.monthlyFee = customer.getCustomerType().equalsIgnoreCase("premium")? 0: MONTHLY_FEE;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Account ID: " + accountNumber);
        System.out.printf("Customer: %s (%s)\n", customer.getName(), customer.getCustomerType());
        System.out.println("Type: " + getAccountType());
        System.out.printf("Balance: $%.2f (overdraft included)\n", (balance + overdraftLimit));
        System.out.println("Overdraft Limit: $" + overdraftLimit);
        System.out.println("Monthly Fee: $" + monthlyFee);
        System.out.println("Status: " + status);
    }

    @Override
    public String getAccountType() {
        return "Checking";
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

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }
}
