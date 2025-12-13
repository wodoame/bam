package com.bam.services;

import com.bam.exceptions.InvalidAccountException;
import com.bam.models.Account;
import com.bam.models.CheckingAccount;
import com.bam.models.RegularCustomer;
import com.bam.models.SavingsAccount;
import com.bam.models.Transaction;
import com.bam.utils.InputHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AccountManager {
    private final ArrayList<Account> accounts;
    private final Map<String, Account> accountLookup;
    private final InputHandler inputHandler;

    public AccountManager(InputHandler inputHandler) {
        this.accounts = new ArrayList<>();
        this.accountLookup = new HashMap<>();
        this.inputHandler = inputHandler;
    }

    // By default, this method should show success message and the customer details
    public void addAccount(Account account) {
        addAccount(account, false);
    }

    public void addAccount(Account account, boolean silent) {
        if (accountLookup.putIfAbsent(account.getAccountNumber(), account) != null) {
            throw new IllegalArgumentException(
                    "Account number " + account.getAccountNumber() + " already exists.");
        }
        accounts.add(account);
        if (!silent) {
            System.out.println("\nAccount created successfully!");
            account.displayAccountDetails();
            System.out.println("\nPress Enter to continue...");
            inputHandler.waitForEnter();
        }
    }

    public Account findAccount(String accountNumber) {
        Account account = accountLookup.get(accountNumber);
        if (account == null) {
            throw new InvalidAccountException("Account number " + accountNumber + " not found.");
        }
        return account;
    }

    public void viewAllAccounts() {
        System.out.println("ACCOUNT LISTING");
        final String headerFormat = "%-10s | %-20s | %-30s | %-12s | %-10s%n";
        final String rowFormat = "%-10s | %-20s | %-30s | %-12s | %-10s%n";
        final int tableWidth = 10 + 3 + 20 + 3 + 30 + 3 + 12 + 3 + 10; // column widths plus separator spacing
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "ACC NO", "CUSTOMER NAME", "TYPE", "BALANCE", "STATUS");
        System.out.println(divider);
        accounts.stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .forEach(account -> {
                    // For checking accounts, show balance + overdraft limit
                    String balanceValue;
                    if (account instanceof CheckingAccount checkingAcc) {
                        double totalAvailable = account.getBalance() + checkingAcc.getOverdraftLimit();
                        balanceValue = String.format("$%.2f", totalAvailable);
                    } else {
                        balanceValue = String.format("$%.2f", account.getBalance());
                    }

                    // Print main account row
                    System.out.printf(
                            rowFormat,
                            account.getAccountNumber(),
                            account.getCustomer().getName(),
                            account.getAccountType(),
                            balanceValue,
                            account.getStatus()
                    );

                    // Print account-specific details on the next line
                    if (account instanceof SavingsAccount savingsAcc) {
                        System.out.printf("%-10s | %-20s | %-30s |%n",
                                "",
                                "",
                                String.format("Interest Rate: %.1f%%", savingsAcc.getInterestRate()));
                        System.out.printf("%-10s | %-20s | %-30s |%n",
                                "",
                                "",
                                String.format("Min Balance: $%.0f", savingsAcc.getMinimumBalance()));
                    } else if (account instanceof CheckingAccount checkingAcc) {
                        System.out.printf("%-10s | %-20s | %-30s |%n",
                                "",
                                "",
                                String.format("Overdraft Limit: $%.0f", checkingAcc.getOverdraftLimit()));
                        System.out.printf("%-10s | %-20s | %-30s |%n",
                                "",
                                "",
                                String.format("Monthly Fee: $%.0f", checkingAcc.getMonthlyFee()));
                    }

                    // Add row separator after each account
                    System.out.println(divider);
                });
        System.out.println("Total Accounts: " + accounts.size());
        System.out.printf("Total Bank Balance: $%.2f%n", getTotalBalance());
        System.out.println("\nPress Enter to continue...");
        inputHandler.waitForEnter();
    }

    public double getTotalBalance() {
        return accounts.stream().mapToDouble(Account::getBalance).sum();
    }

    public void generateSeedAccounts(TransactionManager transactionManager) {
        String[] names = {"Bernard", "Alice", "John", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack"};
        Random random = new Random();

        for (String name: names){
            double initialDeposit = 500 + (random.nextDouble() * 1500); // Random deposit between 500 and 2000
            RegularCustomer customer = new RegularCustomer(name, 30, "0123456789", "Accra, Ghana");

            // Randomly choose between Savings (0) and Checking (1) account
            Account account;
            if (random.nextBoolean()) {
                account = new SavingsAccount(customer, initialDeposit);
            } else {
                account = new CheckingAccount(customer, initialDeposit);
            }

            addAccount(account, true);

            // For checking accounts, the balance after should include overdraft limit
            double balanceAfter;
            if (account instanceof CheckingAccount checkingAcc) {
                balanceAfter = account.getBalance() + checkingAcc.getOverdraftLimit();
            } else {
                balanceAfter = account.getBalance();
            }

            Transaction txn = new Transaction(account.getAccountNumber(), "Deposit", initialDeposit, balanceAfter);
            transactionManager.addTransaction(txn);
        }
    }

    public int getAccountCount() {
        return accounts.size();
    }

    public ArrayList<Account> getAccountsSnapshot() {
        return new ArrayList<>(accounts);
    }
}
