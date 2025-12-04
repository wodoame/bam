package com.bam.utils;
import com.bam.exceptions.*;
import com.bam.models.SavingsAccount;

public class InputValidator {
    public void validateAge(int age) {
        if (age < 18 || age > 100) {
            throw new InvalidAgeException("Age must be between 18 and 100.");
        }
    }

    public void validateName(String name) {
        if (name.trim().isEmpty()) {
            throw new InvalidNameException("Name cannot be empty.");
        }
        if (!name.matches("[a-zA-Z ]+")) {
            throw new InvalidNameException("Name can only contain letters and spaces.");
        }
    }

    public void validateContact(String contact) {
        if (!contact.matches("\\d{10}")) {
            throw new InvalidContactException("Contact number must be exactly 10 digits.");
        }
    }

    public void validateInitialDepositAmount(double amount, String accountType) {
        if(accountType.equalsIgnoreCase("savings")) {
            if(amount < SavingsAccount.MINIMUM_BALANCE) {
                throw new InsufficientInitialDepositException("Initial deposit for savings account must be at least " + SavingsAccount.MINIMUM_BALANCE);
            }
        }
    }

    public void validateAccountTypeChoice(int choice){
        if (choice != 1 && choice != 2) {
            throw new InvalidChoiceException("Please select a valid option (1 or 2)");
        }
    }

    public void validateCustomerTypeChoice(int choice){
        if (choice != 1 && choice != 2) {
            throw new InvalidChoiceException("Please select a valid option (1 or 2)");
        }
    }
}
