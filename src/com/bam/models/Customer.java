package com.bam.models;

/**
 * Base abstraction for bank customers, providing identity and contact info.
 */
public abstract class Customer {
    protected String customerId;
    protected String name;
    protected int age;
    protected String contact;
    protected String email;
    protected String address;
    protected static int customerCounter = 1;

    /**
     * Creates a new customer with an auto-generated ID.
     */
    public Customer(String name, int age, String contact, String email, String address) {
        this(name, age, contact, email, address, null, true);
    }

    /**
     * Rehydrates a customer from persisted data.
     */
    protected Customer(String name, int age, String contact, String email, String address, String customerId) {
        this(name, age, contact, email, address, customerId, false);
    }

    private Customer(String name, int age, String contact, String email, String address, String customerId, boolean autoGenerateId) {
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.email = email;
        this.address = address;
        if (autoGenerateId) {
            this.customerId = generateCustomerId();
        } else {
            if (customerId == null || customerId.isBlank()) {
                throw new IllegalArgumentException("Customer ID must be provided for persisted customers.");
            }
            this.customerId = customerId;
        }
    }

    /**
     * Assigns the next sequential customer ID.
     */
    private String generateCustomerId() {
        return String.format("CUST%03d", customerCounter++);
    }

    /** Prints customer-specific details. */
    public abstract void displayCustomerDetails();

    /** @return label describing the customer tier. */
    public abstract String getCustomerType();

    /** @return generated customer identifier. */
    public String getCustomerId() {
        return customerId;
    }

    /** @return full name. */
    public String getName() {
        return name;
    }

    /** @return age in years. */
    public int getAge() {
        return age;
    }

    /** @return contact phone number. */
    public String getContact() {
        return contact;
    }

    /** @return email address. */
    public String getEmail() {
        return email;
    }

    /** @return postal address. */
    public String getAddress() {
        return address;
    }

    /**
     * Updates static counter so newly created customers do not collide with persisted ones.
     */
    public static void setCustomerCounter(int nextCounter) {
        if (nextCounter > 0) {
            customerCounter = nextCounter;
        }
    }
}
