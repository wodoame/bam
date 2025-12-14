package com.bam.models;

/**
 * Standard customer tier without premium perks.
 */
public class RegularCustomer extends Customer {

    /**
     * Constructs a new regular customer using generated ID.
     */
    public RegularCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    /**
     * Rehydrates a persisted regular customer record.
     */
    public RegularCustomer(String name, int age, String contact, String address, String customerId) {
        super(name, age, contact, address, customerId);
    }

    /** {@inheritDoc} */
    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + name);
        System.out.println("Type: " + getCustomerType());
        System.out.println("Contact: " + contact);
        System.out.println("Address: " + address);
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerType() {
        return "Regular";
    }
}
