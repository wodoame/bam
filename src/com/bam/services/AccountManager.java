package com.bam.services;

import com.bam.exceptions.InvalidAccountException;
import com.bam.exceptions.InvalidAccountNumberException;
import com.bam.models.Account;
import com.bam.models.CheckingAccount;
import com.bam.models.Customer;
import com.bam.models.RegularCustomer;
import com.bam.models.SavingsAccount;
import com.bam.models.Transaction;
import com.bam.utils.InputHandler;
import com.bam.utils.InputValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Coordinates all account CRUD operations, persistence, and initial data seeding.
 */
public class AccountManager {
    private final Map<String, Account> accountLookup;
    private final InputHandler inputHandler;
    private final FilePersistenceService filePersistenceService;
    private final TransactionManager transactionManager;
    private final InputValidator validator = new InputValidator();

    /**
     * Creates a manager with interactive input handling and transaction coordination.
     */
    public AccountManager(InputHandler inputHandler, TransactionManager transactionManager) {
        this.accountLookup = new HashMap<>();
        this.inputHandler = inputHandler;
        this.transactionManager = transactionManager;
        this.filePersistenceService = new FilePersistenceService();
    }

    // By default, this method should show success message and the customer details
    /**
     * Registers a newly created account and displays confirmation output.
     */
    public void addAccount(Account account) {
        addAccount(account, false);
    }

    /**
     * Registers an account, optionally suppressing user-facing output.
     */
    public void addAccount(Account account, boolean silent) {
        if (accountLookup.putIfAbsent(account.getAccountNumber(), account) != null) {
            throw new IllegalArgumentException(
                    "Account number " + account.getAccountNumber() + " already exists.");
        }
        if (!silent) {
            System.out.println("\nAccount created successfully!");
            account.displayAccountDetails();
            System.out.println("\nPress Enter to continue...");
            inputHandler.waitForEnter();
            saveAllData();
        }
    }

    /**
     * Finds an account by number or throws if not present.
     *
     * @throws InvalidAccountException when the account does not exist
     */
    public Account findAccount(String accountNumber) throws InvalidAccountException, InvalidAccountNumberException {
        validator.validateAccountNumberFormat(accountNumber);
        Account account = accountLookup.get(accountNumber);
        if (account == null) {
            throw new InvalidAccountException("Account number " + accountNumber + " not found.");
        }
        return account;
    }

    /**
     * Prints a tabular overview of all accounts, including derived stats.
     */
    public void viewAllAccounts() {
        System.out.println("ACCOUNT LISTING");
        final String headerFormat = "%-10s | %-20s | %-30s | %-12s | %-10s%n";
        final String rowFormat = "%-10s | %-20s | %-30s | %-12s | %-10s%n";
        final int tableWidth = 10 + 3 + 20 + 3 + 30 + 3 + 12 + 3 + 10; // column widths plus separator spacing
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "ACC NO", "CUSTOMER NAME", "TYPE", "BALANCE", "STATUS");
        System.out.println(divider);
        accountLookup.values().stream()
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
        System.out.println("Total Accounts: " + accountLookup.size());
        System.out.printf("Total Bank Balance: $%.2f%n", getTotalBalance());
        System.out.println("\nPress Enter to continue...");
        inputHandler.waitForEnter();
    }

    /**
     * @return aggregate balance across all accounts.
     */
    public double getTotalBalance() {
        return accountLookup.values().stream().mapToDouble(Account::getBalance).sum();
    }

    /**
     * Seeds sample accounts and matching transactions for demo purposes.
     */
    public void generateSeedAccounts(TransactionManager transactionManager) {
        String[] names = {"Bernard", "Alice", "John", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack"};
        Random random = new Random();

        for (String name: names){
            double initialDeposit = 500 + (random.nextDouble() * 1500); // Random deposit between 500 and 2000
            RegularCustomer customer = new RegularCustomer(name, 30, "0123456789", name.toLowerCase() + "@example.com", "Accra, Ghana");

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

    /** @return live count of managed accounts. */
    public int getAccountCount() {
        return accountLookup.size();
    }

    /** @return defensive copy of the current accounts list. */
    public ArrayList<Account> getAccountsSnapshot() {
        return new ArrayList<>(accountLookup.values());
    }

    /**
     * Attempts to load account and transaction data from disk, falling back to seed data.
     */
    public void loadPersistedData(TransactionManager transactionManager) {
        try {
            var loadedAccounts = filePersistenceService.loadAccounts();
            var loadedTransactions = filePersistenceService.loadTransactions();
            accountLookup.clear();
            loadedAccounts.forEach(account ->
                accountLookup.put(account.getAccountNumber(), account)
            );
            TransactionManager.seedTransactions(loadedTransactions);
            syncCounters();
            if (accountLookup.isEmpty()) {
                System.out.println("No persisted accounts found. Generating seed data...");
                generateSeedAccounts(transactionManager);
            } else {
                System.out.printf("Loaded %d accounts and %d transactions from disk.%n", accountLookup.size(), loadedTransactions.size());
            }
        } catch (IOException e) {
            System.out.println("Failed to load persisted data: " + e.getMessage());
            System.out.println("Generating default seed data instead.");
            generateSeedAccounts(transactionManager);
        }
    }

    /**
     * Initializes data at startup, preferring persisted state and seeding otherwise.
     */
    public void initializeData() {
        if (!loadPersistedData()) {
            generateSeedAccounts(transactionManager);
            saveAllData();
        }
    }

    private boolean loadPersistedData() {
        try {
            var loadedAccounts = filePersistenceService.loadAccounts();
            var loadedTransactions = filePersistenceService.loadTransactions();
            accountLookup.clear();
            loadedAccounts.forEach(account ->
                accountLookup.put(account.getAccountNumber(), account)
            );
            TransactionManager.seedTransactions(loadedTransactions);
            syncCounters();
            if (accountLookup.isEmpty()) {
                return false;
            }
            System.out.printf("Loaded %d accounts and %d transactions from disk.%n", accountLookup.size(), loadedTransactions.size());
            return true;
        } catch (IOException e) {
            System.out.println("Failed to load persisted data: " + e.getMessage());
            return false;
        }
    }

    private void syncCounters() {
        int maxAccount = accountLookup.values().stream()
                .map(Account::getAccountNumber)
                .map(str -> str.replace("ACC", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        Account.setAccountCounter(maxAccount + 1);

        int maxCustomer = accountLookup.values().stream()
                .map(Account::getCustomer)
                .map(Customer::getCustomerId)
                .map(id -> id.replace("CUST", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        Customer.setCustomerCounter(maxCustomer + 1);
    }

    /**
     * Saves current accounts and transactions to disk.
     */
    public void saveAllData() {
        try {
            filePersistenceService.saveAccounts(new ArrayList<>(accountLookup.values()));
            filePersistenceService.saveTransactions(TransactionManager.allTransactions());
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }
}
