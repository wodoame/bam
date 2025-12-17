package com.bam.services;

import com.bam.models.*;
import com.bam.utils.InputValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles saving and loading of accounts and transactions using a simple delimited text format.
 */
public class FilePersistenceService {
    private static final String DATA_DIR = "data";
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String DELIMITER = "|";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.US);

    private final Path dataDirectory;
    private final Path accountsPath;
    private final Path transactionsPath;
    private final InputValidator validator = new InputValidator();

    public FilePersistenceService() {
        this.dataDirectory = Path.of(DATA_DIR);
        this.accountsPath = dataDirectory.resolve(ACCOUNTS_FILE);
        this.transactionsPath = dataDirectory.resolve(TRANSACTIONS_FILE);
    }

    /**
     * Ensures the data directory exists before reading or writing.
     */
    public void ensureDataDirectory() throws IOException {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
        }
    }

    /**
     * Loads all accounts from disk, returning an empty list if the file is absent.
     */
    public List<Account> loadAccounts() throws IOException {
        ensureDataDirectory();
        if (Files.notExists(accountsPath)) {
            return List.of();
        }
        try (Stream<String> lines = Files.lines(accountsPath, StandardCharsets.UTF_8)) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(this::parseAccount)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Persists the provided accounts to disk, overwriting the previous file.
     */
    public void saveAccounts(List<Account> accounts) throws IOException {
        ensureDataDirectory();
        List<String> lines = accounts.stream()
                .map(this::formatAccount)
                .toList();
        Files.write(accountsPath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    /**
     * Loads all transactions from disk, returning an empty list if the file is absent.
     */
    public List<Transaction> loadTransactions() throws IOException {
        ensureDataDirectory();
        if (Files.notExists(transactionsPath)) {
            return List.of();
        }
        try (Stream<String> lines = Files.lines(transactionsPath, StandardCharsets.UTF_8)) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(this::parseTransaction)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Persists transactions to disk, overwriting the previous file.
     */
    public void saveTransactions(List<Transaction> transactions) throws IOException {
        ensureDataDirectory();
        List<String> lines = transactions.stream()
                .map(this::formatTransaction)
                .toList();
        Files.write(transactionsPath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    /**
     * Parses an account row from the delimited text format.
     */
    private Account parseAccount(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 12) {
            throw new IllegalArgumentException("Invalid account entry: " + line);
        }
        String accountNumber = parts[0];
        String accountType = parts[1];
        double balance = Double.parseDouble(parts[2]);
        String status = parts[3];
        Customer customer = getCustomer(parts);

        validator.validateAccountNumberFormat(accountNumber);
        validator.validateContact(customer.getContact());
        validator.validateEmail(customer.getEmail());

        return switch (accountType.toLowerCase()) {
            case "savings" -> new SavingsAccount(customer, balance, accountNumber, status);
            case "checking" -> new CheckingAccount(customer, balance, accountNumber, status);
            default -> throw new IllegalArgumentException("Unsupported account type: " + accountType);
        };
    }

    private static Customer getCustomer(String[] parts) {
        String customerType = parts[4];
        String customerId = parts[5];
        String name = parts[6];
        int age = Integer.parseInt(parts[7]);
        String contact = parts[8];
        String email = parts[9];
        String address = parts[10];

        return customerType.equalsIgnoreCase("Premium")
                ? new PremiumCustomer(name, age, contact, email, address, customerId)
                : new RegularCustomer(name, age, contact, email, address, customerId);
    }

    /**
     * Serializes an account to the delimited text format.
     */
    private String formatAccount(Account account) {
        Customer customer = account.getCustomer();
        double extra = account instanceof CheckingAccount
                ? ((CheckingAccount) account).getOverdraftLimit()
                : ((SavingsAccount) account).getInterestRate();
        return String.join(DELIMITER,
                account.getAccountNumber(),
                account.getAccountType(),
                String.valueOf(account.getBalance()),
                account.getStatus(),
                customer.getCustomerType(),
                customer.getCustomerId(),
                sanitize(customer.getName()),
                String.valueOf(customer.getAge()),
                customer.getContact(),
                sanitize(customer.getEmail()),
                sanitize(customer.getAddress()),
                String.valueOf(extra));
    }

    /**
     * Parses a transaction row from the delimited text format.
     */
    private Transaction parseTransaction(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid transaction entry: " + line);
        }
        String txnId = parts[0];
        String accountNumber = parts[1];
        String type = parts[2];
        double amount = Double.parseDouble(parts[3]);
        double balanceAfter = Double.parseDouble(parts[4]);
        Instant instant = Instant.parse(parts[5]);

        Transaction txn = new Transaction(txnId.isBlank() ? null : txnId, accountNumber, type, amount, balanceAfter, Date.from(instant));
        if (txnId != null && !txnId.isBlank()) {
            txn.setTransactionId(txnId);
        }
        txn.setTimestamp(Date.from(instant));
        return txn;
    }

    /**
     * Serializes a transaction to the delimited text format.
     */
    private String formatTransaction(Transaction txn) {
        return String.join(DELIMITER,
                txn.getTransactionId() == null ? "" : txn.getTransactionId(),
                txn.getAccountNumber(),
                txn.getType(),
                String.valueOf(txn.getAmount()),
                String.valueOf(txn.getBalanceAfter()),
                FORMATTER.format(txn.getTimestamp().toInstant()));
    }

    /**
     * Replaces delimiter occurrences to keep files parseable.
     */
    private String sanitize(String value) {
        return value == null ? "" : value.replace(DELIMITER, "/");
    }
}
