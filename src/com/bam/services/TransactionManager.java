package com.bam.services;

import com.bam.models.Account;
import com.bam.models.CheckingAccount;
import com.bam.models.Transaction;
import com.bam.utils.InputHandler;
import com.bam.utils.InputValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manages the in-memory transaction ledger, sorting, persistence integration,
 * and summary statistics for account histories.
 */
public class TransactionManager {
    private  static final Map<String, List<Transaction>> transactionsMap = new HashMap<>();
    private static final Map<String, Integer> transactionCounters = new HashMap<>();
    private static final Object ledgerLock = new Object();
    private final InputHandler inputHandler;
    private final InputValidator validator = new InputValidator();

    /**
     * Creates a manager optionally wired to interactive input handlers.
     */
    public TransactionManager(InputHandler inputHandler){
        this.inputHandler = inputHandler;
    }

    /**
     * Reloads all transactions from persistence, replacing any in-memory state.
     */
    public void reloadTransactions(List<Transaction> persistedTransactions) {
        synchronized (ledgerLock) {
            transactionsMap.clear();
            transactionCounters.clear();
            persistedTransactions.forEach(txn -> {
                transactionsMap.computeIfAbsent(txn.getAccountNumber(), key -> new ArrayList<>()).add(txn);
                updateCounter(txn);
            });
        }
    }

    /**
     * Snapshot of every transaction across accounts.
     */
    public List<Transaction> snapshotAllTransactions() {
        synchronized (ledgerLock) {
            return transactionsMap.values().stream()
                    .flatMap(List::stream)
                    .map(TransactionManager::cloneTransaction)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Seeds the static map for scenarios without a TransactionManager instance.
     */
    public static void seedTransactions(List<Transaction> transactions) {
        synchronized (ledgerLock) {
            transactionsMap.clear();
            transactionCounters.clear();
            transactions.forEach(txn -> {
                transactionsMap.computeIfAbsent(txn.getAccountNumber(), key -> new ArrayList<>()).add(txn);
                updateCounter(txn);
            });
        }
    }

    /**
     * @return view of all transactions currently tracked.
     */
    public static List<Transaction> allTransactions() {
        synchronized (ledgerLock) {
            return transactionsMap.values().stream()
                    .flatMap(List::stream)
                    .map(TransactionManager::cloneTransaction)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Computes the projected balance after applying the provided transaction type.
     */
    public static double getBalanceAfter(Account account, double amount, String transactionType) {
        if (transactionType.equalsIgnoreCase("deposit") || transactionType.equalsIgnoreCase("transfer in")) {
            if (account.getAccountType().equalsIgnoreCase("Checking")) {
                // For checking accounts, include overdraft limit in balance calculation
                return account.getBalance() + CheckingAccount.OVERDRAFT_LIMIT + amount;
            }
            return account.getBalance() + amount; // For savings accounts
        } else if (transactionType.equalsIgnoreCase("withdrawal") || transactionType.equalsIgnoreCase("transfer out")) {
            if (account.getAccountType().equalsIgnoreCase("Checking")) {
                // For checking accounts, include overdraft limit in balance calculation
                return account.getBalance() + CheckingAccount.OVERDRAFT_LIMIT - amount;
            }
            return account.getBalance() - amount;
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
    }

    /**
     * Adds a transaction and ensures it has a generated ID.
     */
    public void addTransaction(Transaction transaction) {
        synchronized (ledgerLock) {
            transactionsMap
                    .computeIfAbsent(transaction.getAccountNumber(), key -> new ArrayList<>())
                    .add(transaction);
            generateTransactionId(transaction);
        }
    }

    /**
     * Displays the transaction history for a single account, including sorting prompt.
     */
    public void viewTransactionsByAccount(String accountNumber) {
        validator.validateAccountNumberFormat(accountNumber);
        System.out.println("TRANSACTION HISTORY FOR ACCOUNT NUMBER " + accountNumber);
        final List<Transaction> accountTransactions = getTransactions(accountNumber);
        final List<Transaction> sortedTransactions = sortTransactions(accountTransactions);
        printTransactionsTable(sortedTransactions);
        if (!sortedTransactions.isEmpty()) {
            printSummary(accountNumber);
            if (inputHandler != null) {
                System.out.println("\nPress Enter to continue...");
                inputHandler.waitForEnter();
            }
        }
    }

    /**
     * Aggregates total transaction amounts by type.
     */
    public double calculateTotalTransaction(String accountNumber, String type ) {
        return getTransactions(accountNumber).stream()
                .filter(txn -> txn.getType().equalsIgnoreCase(type))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Prints a rich statement for the provided account, including transactions and summary.
     */
    public void generateStatement(com.bam.models.Account account) {
        validator.validateAccountNumberFormat(account.getAccountNumber());
        String accountNumber = account.getAccountNumber();
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ACCOUNT STATEMENT");
        System.out.println("=".repeat(70));
        System.out.println("\nACCOUNT INFORMATION:");
        System.out.println("-".repeat(70));
        System.out.printf("%-25s: %s%n", "Account Number", accountNumber);
        System.out.printf("%-25s: %s%n", "Account Holder", account.getCustomer().getName());
        System.out.printf("%-25s: %s%n", "Account Type", account.getAccountType());
        System.out.printf("%-25s: $%.2f%n", "Current Balance", account.getBalance());
        System.out.printf("%-25s: %s%n", "Account Status", "Active");

        System.out.println("\nTransactions:");
        List<Transaction> accountTransactions = getTransactions(accountNumber);
        List<Transaction> sortedTransactions = sortTransactions(accountTransactions);
        printTransactionsTable(sortedTransactions);

        // Display summary statistics
        printSummary(accountNumber);
        System.out.println("\n✓ Statement generated successfully.");
        if (inputHandler != null) {
            System.out.println("\nPress Enter to continue...");
            inputHandler.waitForEnter();
        }
    }

    /**
     * @return immutable list of transactions for the supplied account number.
     */
    public static List<Transaction> getTransactions(String accountNumber) {
        return transactionsMap.getOrDefault(accountNumber, List.of());
    }

    /**
     * Filters transactions for a specific account using the provided predicate.
     */
    public List<Transaction> getTransactionsMatching(String accountNumber, Predicate<Transaction> predicate) {
        return getTransactions(accountNumber).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * @return the latest transaction, if any, for the account.
     */
    public Optional<Transaction> getMostRecentTransaction(String accountNumber) {
        return getTransactions(accountNumber).stream()
                .max(Comparator.comparing(Transaction::getTimestamp));
    }

    /**
     * Prints summary information showing totals by transaction type.
     */
    private void printSummary(String accountNumber) {
        double totalDeposits = calculateTotalTransaction(accountNumber, "deposit");
        double totalWithdrawals = calculateTotalTransaction(accountNumber, "withdrawal");
        double totalTransfersIn = calculateTotalTransaction(accountNumber, "transfer in");
        double totalTransfersOut = calculateTotalTransaction(accountNumber, "transfer out");
        double netChange = (totalDeposits + totalTransfersIn) - (totalWithdrawals + totalTransfersOut);
        System.out.println("\nSUMMARY:");
        System.out.printf("Total Deposits:     +$%.2f%n", totalDeposits);
        System.out.printf("Total Withdrawals:  -$%.2f%n", totalWithdrawals);
        System.out.printf("Total Transfers In: +$%.2f%n", totalTransfersIn);
        System.out.printf("Total Transfers Out: -$%.2f%n", totalTransfersOut);
        System.out.printf("Net Change:         %s$%.2f%n", netChange >= 0 ? "+" : "", netChange);
    }

    /**
     * Sorts according to interactive preferences (if available) or defaults.
     */
    private List<Transaction> sortTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return transactions;
        }
        Comparator<Transaction> comparator = resolveSortComparator();
        return transactions.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Determines which comparator to use based on interactive choices.
     */
    private Comparator<Transaction> resolveSortComparator() {
        if (inputHandler == null) {
            return Comparator.comparing(Transaction::getTimestamp).reversed();
        }
        System.out.println("\nChoose how to sort transactions:");
        int fieldChoice = inputHandler.getTransactionSortFieldChoice();
        int directionChoice = inputHandler.getTransactionSortDirectionChoice();
        return buildComparator(fieldChoice, directionChoice);
    }

    /**
     * Builds a comparator for the selected field and direction.
     */
    private Comparator<Transaction> buildComparator(int fieldChoice, int directionChoice) {
        Comparator<Transaction> comparator = switch (fieldChoice) {
            case 2 -> Comparator.comparingDouble(Transaction::getAmount);
            case 3 -> Comparator.comparing(txn -> txn.getType().toLowerCase());
            case 4 -> Comparator.comparingDouble(Transaction::getBalanceAfter);
            default -> Comparator.comparing(Transaction::getTimestamp);
        };
        if (directionChoice == 2) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    /**
     * Prints the table of transactions, including headers and empty state messaging.
     */
    private void printTransactionsTable(List<Transaction> transactions) {
        final String headerFormat = "%-10s | %-12s | %-12s | %-12s | %-25s%n";
        final String rowFormat = "%-10s | %-12s | %-12s | %-12s | %-25s%n";
        final int tableWidth = 10 + 3 + 12 + 3 + 12 + 3 + 12 + 3 + 25;
        final String divider = "-".repeat(tableWidth);
        System.out.println(divider);
        System.out.printf(headerFormat, "TXN ID", "TYPE", "AMOUNT", "BALANCE", "DATE/TIME");
        System.out.println(divider);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this account.");
            System.out.println(divider);
            return;
        }
        transactions.forEach(txn -> {
            String type = txn.getType().toUpperCase();
            String amountValue = formatSignedAmount(txn);
            String balanceValue = String.format("$%.2f", txn.getBalanceAfter());
            System.out.printf(
                    rowFormat,
                    txn.getTransactionId(),
                    type,
                    amountValue,
                    balanceValue,
                    txn.getTimestamp().toString());
        });
        System.out.println(divider);
    }

    /**
     * Renders amounts with +/- signage for clarity.
     */
    private String formatSignedAmount(Transaction txn) {
        boolean isCredit = txn.getType().equalsIgnoreCase("deposit") || txn.getType().equalsIgnoreCase("transfer in");
        String sign = isCredit ? "+" : "-";
        return String.format("%s$%.2f", sign, txn.getAmount());
    }

    private static void generateTransactionId(Transaction transaction) {
        String accountNumber = transaction.getAccountNumber();
        int nextId = transactionCounters.merge(accountNumber, 1, Integer::sum);
        transaction.setTransactionId(String.format("TXN%03d", nextId));
    }

    private static void updateCounter(Transaction transaction) {
        String accountNumber = transaction.getAccountNumber();
        if (transaction.getTransactionId() == null) {
            return;
        }
        try {
            int existing = Integer.parseInt(transaction.getTransactionId().replace("TXN", ""));
            transactionCounters.merge(accountNumber, existing, Math::max);
        } catch (NumberFormatException ignored) {
        }
    }

    private static Transaction cloneTransaction(Transaction txn) {
        Transaction clone = new Transaction(txn.getTransactionId(), txn.getAccountNumber(), txn.getType(), txn.getAmount(), txn.getBalanceAfter(), txn.getTimestamp());
        clone.setTransactionId(txn.getTransactionId());
        clone.setTimestamp(txn.getTimestamp());
        return clone;
    }
}
