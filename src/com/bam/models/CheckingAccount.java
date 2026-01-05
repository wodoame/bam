package com.bam.models;

import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.OverdraftExceededException;
import com.bam.utils.InputValidator;


/**
 * Checking account variant with overdraft support and optional monthly fees.
 */
public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double monthlyFee;
    public static final double OVERDRAFT_LIMIT = 1000.0;
    public static final double MONTHLY_FEE = 10.0;

    /**
     * Creates a checking account that auto-generates an account number.
     */
    public CheckingAccount(Customer customer, double initialDeposit) {
        super(customer, initialDeposit);
        this.overdraftLimit = OVERDRAFT_LIMIT;
        this.monthlyFee = customer.getCustomerType().equalsIgnoreCase("premium")? 0: MONTHLY_FEE;
    }
    /**
     * Rehydrates a checking account from persisted state.
     */
    public CheckingAccount(Customer customer, double balance, String accountNumber, String status) {
        super(customer, balance, accountNumber, status);
        this.overdraftLimit = OVERDRAFT_LIMIT;
        this.monthlyFee = customer.getCustomerType().equalsIgnoreCase("premium") ? 0 : MONTHLY_FEE;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public String getAccountType() {
        return "Checking";
    }

    /**
     * Validates and withdraws funds from the checking account balance,
     * allowing overdraft up to the configured limit.
     *
     * @param amount amount to subtract
     * @return {@code true} when the withdrawal succeeds
     */
    @Override
    public boolean withdraw(double amount) throws OverdraftExceededException, InvalidWithdrawalAmountException {
        InputValidator validator = new InputValidator();
        synchronized (this) {
            validator.validateCheckingWithdrawal(amount, balance);
            balance -= amount;
            return true;
        }
    }

    /**
     * Deducts the monthly maintenance fee unless the customer qualifies for a waiver.
     */
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

    /** @return overdraft buffer available for withdrawals. */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /** @return monthly fee charged to non-premium customers. */
    public double getMonthlyFee() {
        return monthlyFee;
    }
}
