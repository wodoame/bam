package com.bam.models;

/**
 * Premium tier customer with waived fees and priority perks.
 */
public class PremiumCustomer extends Customer {
    private final double minimumBalanceRequirement = 10000.0;

    /**
     * Creates a premium customer, auto-generating an ID.
     */
    public PremiumCustomer(String name, int age, String contact, String email, String address) {
        super(name, age, contact, email, address);
    }

    /**
     * Rehydrates a premium customer from persisted state.
     */
    public PremiumCustomer(String name, int age, String contact, String email, String address, String customerId) {
        super(name, age, contact, email, address, customerId);
    }

    /** {@inheritDoc} */
    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + name);
        System.out.println("Type: " + getCustomerType());
        System.out.println("Contact: " + contact);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
        System.out.println("Benefits: No monthly fees, Priority service");
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerType() {
        return "Premium";
    }

    /**
     * Indicates whether this customer qualifies for waived fees.
     */
    public boolean hasWaivedFees() {
        return true;
    }
}
