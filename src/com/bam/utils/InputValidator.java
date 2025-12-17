package com.bam.utils;

import com.bam.exceptions.*;
import com.bam.models.CheckingAccount;
import com.bam.models.SavingsAccount;

import java.util.regex.Pattern;

/**
 * Performs reusable validation for user inputs across the CLI.
 */
public class InputValidator {
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("ACC\\d{3}");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("\\d{10}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Ensures the applicant is at least 18 years old. */
    public void validateAge(int age) {
        if (age < 18) {
            throw new InvalidAgeException("You must be at least 18 years old to create an account.");
        }
    }

    /** Validates that a name is non-empty and alphabetic. */
    public void validateName(String name) {
        if (name.trim().isEmpty()) {
            throw new InvalidNameException("Name cannot be empty.");
        }
        if (!name.matches("[a-zA-Z ]+")) {
            throw new InvalidNameException("Name can only contain letters and spaces.");
        }
    }

    /** Requires a 10-digit numeric contact number. */
    public void validateContact(String contact) {
        if (!CONTACT_PATTERN.matcher(contact).matches()) {
            throw new InvalidContactException("Contact number must be exactly 10 digits.");
        }
    }

    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format. Example: user@example.com");
        }
    }

    /** Enforces minimum opening deposits based on account type. */
    public void validateInitialDepositAmount(double amount, String accountType) {
        if (accountType.equalsIgnoreCase("savings")) {
            if (amount < SavingsAccount.MINIMUM_BALANCE) {
                throw new InsufficientInitialDepositException(
                        "Initial deposit for savings account must be at least " + SavingsAccount.MINIMUM_BALANCE);
            }
        }
    }

    /** Accepts only supported account type menu selections. */
    public void validateAccountTypeChoice(int choice) {
        if (choice != 1 && choice != 2) {
            throw new InvalidChoiceException("Please select a valid option (1 or 2)");
        }
    }

    /** Accepts only supported customer type menu selections. */
    public void validateCustomerTypeChoice(int choice) {
        if (choice != 1 && choice != 2) {
            throw new InvalidChoiceException("Please select a valid option (1 or 2)");
        }
    }

    /** Accepts only supported transaction type menu selections. */
    public void validateTransactionTypeChoice(int choice) {
        if (choice != 1 && choice != 2 && choice != 3) {
            throw new InvalidChoiceException("Please select a valid option (1, 2 or 3)");
        }
    }

    /** Accepts only supported transaction sort field choices. */
    public void validateSortFieldChoice(int choice) {
        if (choice < 1 || choice > 4) {
            throw new InvalidChoiceException("Please select a valid option (1 - 4)");
        }
    }

    /** Accepts only ascending or descending selection. */
    public void validateSortDirectionChoice(int choice) {
        if (choice != 1 && choice != 2) {
            throw new InvalidChoiceException("Please select a valid option (1 or 2)");
        }
    }

    /** Ensures deposits are positive. */
    public void validateDepositAmount(double amount) {
        if (amount <= 0) {
            throw new InvalidDepositAmountException("Deposit amount must be greater than zero.");
        }
    }

    /** Validates withdrawal limits for savings accounts, enforcing minimum balance. */
    public void validateSavingsWithdrawal(double amount, double currentBalance) {
        if (amount <= 0) {
            throw new InvalidWithdrawalAmountException("Withdrawal amount must be greater than zero.");
        }

        if (currentBalance - amount < SavingsAccount.MINIMUM_BALANCE) {
            throw new InsufficientFundsException(String.format(
                    "You do not have sufficient funds (%.2f) to perform this transaction\n" +
                            "You need a minimum balance of $%.2f in your account",
                    currentBalance, SavingsAccount.MINIMUM_BALANCE));
        }
    }

    /** Validates withdrawal limits for checking accounts, allowing overdraft up to limit. */
    public void validateCheckingWithdrawal(double amount, double currentBalance) {
        if (amount <= 0) {
            throw new InvalidWithdrawalAmountException("Withdrawal amount must be greater than zero.");
        }

        if (currentBalance - amount < -CheckingAccount.OVERDRAFT_LIMIT) {
            throw new OverdraftExceededException(String.format("Overdraft limit exceeded\n" +
                    "You do not have sufficient funds ($%.2f + $%.2f overdraft limit) to perform this transaction",
                    currentBalance, CheckingAccount.OVERDRAFT_LIMIT));
        }
    }

    /** Validates the format of account numbers. */
    public void validateAccountNumberFormat(String accountNumber) {
        if (accountNumber == null || !ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new InvalidAccountNumberException("Account number must match ACC followed by three digits (e.g., ACC123).");
        }
    }
}
