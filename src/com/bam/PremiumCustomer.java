package com.bam;

public class PremiumCustomer extends Customer {
    private final double minimumBalanceRequirement = 10000.0;

    public PremiumCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + name);
        System.out.println("Type: " + getCustomerType());
        System.out.println("Contact: " + contact);
        System.out.println("Address: " + address);
        System.out.println("Benefits: No monthly fees, Priority service");
    }

    @Override
    public String getCustomerType() {
        return "Premium";
    }

    public boolean hasWaivedFees() {
        return true;
    }
}
