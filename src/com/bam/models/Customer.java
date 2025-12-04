package com.bam;

public abstract class Customer {
    protected String customerId;
    protected String name;
    protected int age;
    protected String contact;
    protected String address;
    protected static int customerCounter = 1;

    public Customer(String name, int age, String contact, String address) {
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.address = address;
        this.customerId = generateCustomerId();
    }

    private String generateCustomerId() {
        return String.format("CUST%03d", customerCounter++);
    }

    public abstract void displayCustomerDetails();

    public abstract String getCustomerType();

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }
}
