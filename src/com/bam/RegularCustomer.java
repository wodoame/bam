package com.bam;

public class RegularCustomer extends Customer {

    public RegularCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + name);
        System.out.println("Type: " + getCustomerType());
        System.out.println("Contact: " + contact);
        System.out.println("Address: " + address);
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
}
