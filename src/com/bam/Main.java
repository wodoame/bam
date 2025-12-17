package com.bam;

import com.bam.models.*;
import com.bam.services.AccountManager;
import com.bam.services.TransactionManager;
import com.bam.utils.InputHandler;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.engine.TestExecutionResult;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Entry point for the console-based Bank Account Management application.
 * Drives the interactive menus, coordinates persistence, and exposes a test harness.
 */
public class Main {
    private static final InputHandler inputHandler = new InputHandler();
    private static final TransactionManager transactionManager = new TransactionManager(inputHandler);
    private static final AccountManager accountManager = new AccountManager(inputHandler, transactionManager);

    /**
     * Launches the CLI loop, routing each menu option until the user chooses to exit.
     * All persisted data is initialized before the loop begins and saved on exit.
     */
    public static void main(String[] args) {
        accountManager.initializeData();
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int choice = inputHandler.getIntInput("Enter your choice: ", "Choice must be a number");
            switch (choice) {
                case 1:
                    manageAccountsMenu();
                    break;
                case 2:
                    processTransaction();
                    break;
                case 3:
                    generateAccountStatements();
                    break;
                case 4:
                    handlePersistenceMenu();
                    break;
                case 5:
                    runTests();
                    break;
                case 6:
                    runConcurrentSimulation();
                    break;
                case 7:
                    exit = true;
                    accountManager.saveAllData();
                    System.out.println(
                            "\nThank you for using the Bank Account Management System!\nData saved to disk. Remember to commit your latest changes to Git!\nGoodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        inputHandler.closeScanner();
    }

    /**
     * Handles the Manage Accounts submenu, allowing users to create accounts,
     * view listings, and inspect transaction history until they return to the main menu.
     */
    private static void manageAccountsMenu() {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== Manage Accounts ===");
            System.out.println("1. Create Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. View Transaction History");
            System.out.println("4. Back to Main Menu");
            int choice = inputHandler.getIntInput("Enter your choice: ", "Choice must be a number");
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    accountManager.viewAllAccounts();
                    break;
                case 3:
                    viewTransactionHistory();
                    break;
                case 4:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Prints the top-level navigation menu for the application.
     */
    private static void printMainMenu() {
        System.out.println("\n=== Bank Account Management System ===");
        System.out.println("1. Manage Accounts");
        System.out.println("2. Perform Transactions");
        System.out.println("3. Generate Account Statements");
        System.out.println("4. Save/Load Data");
        System.out.println("5. Run tests");
        System.out.println("6. Run Concurrent Simulation");
        System.out.println("7. Exit");
    }

    /**
     * Prompts the user for customer and account details, creates the account,
     * and seeds an initial deposit transaction to reflect the opening balance.
     */
    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.println("Select Customer Type:");
        System.out.println("1. Regular (Standard banking services)");
        System.out.println("2. Premium (Enhanced benefits, min balance $10,000)");
        int customerTypeChoice = inputHandler.getCustomerTypeChoice("Enter choice: ");
        String name = inputHandler.getName("Enter Customer Name: ");
        int age = inputHandler.getAge("Enter Age: ");
        String contact = inputHandler.getContact("Enter Contact Number: ");
        String email = inputHandler.getEmail("Enter Email Address: ");
        String address = inputHandler.getStringInput("Enter Address: ");

        Customer customer;
        if (customerTypeChoice == 1) {
            customer = new RegularCustomer(name, age, contact, email, address);
        } else {
            customer = new PremiumCustomer(name, age, contact, email, address);
        }

        System.out.println("Select Account Type:");
        System.out.printf("1. Savings (Interest Rate: %.1f, Minimum Balance: $%.2f)\n", SavingsAccount.INTEREST_RATE,
                SavingsAccount.MINIMUM_BALANCE);
        System.out.printf("2. Checking (Overdraft: $%.2f, Monthly Fee: $%.2f)\n", CheckingAccount.OVERDRAFT_LIMIT,
                CheckingAccount.MONTHLY_FEE);
        int accountTypeChoice = inputHandler.getAccountTypeChoice("Enter choice: ");
        double initialDeposit;

        Account account;
        if (accountTypeChoice == 1) {
            initialDeposit = inputHandler.getInitialDeposit("Enter Initial Deposit: ", "savings");
            account = new SavingsAccount(customer, initialDeposit);
        } else {
            initialDeposit = inputHandler.getInitialDeposit("Enter Initial Deposit: ", "checking");
            account = new CheckingAccount(customer, initialDeposit);
        }
        accountManager.addAccount(account);
        // Initial deposit transaction
        Transaction txn = new Transaction(account.getAccountNumber(), "Deposit", initialDeposit, account.getBalance());
        transactionManager.addTransaction(txn);
    }

    /**
     * Shows a confirmation dialog for a prepared transaction.
     *
     * @param txn transaction preview to display to the user
     * @return {@code true} if the user confirms; {@code false} otherwise
     */
    private static boolean showTransactionConfirmationPrompt(Transaction txn) {
        System.out.println("\nTRANSACTION CONFIRMATION");
        System.out.println("________________________________");
        txn.displayTransactionDetails();
        String confirmationChoice = inputHandler.getStringInput("Confirm transaction? (Y/N): ");
        if (!confirmationChoice.equalsIgnoreCase("Y")) {
            System.out.println("Transaction cancelled.");
            return false;
        }
        return true;
    }

    /**
     * Guides the user through deposit, withdrawal, or transfer workflows,
     * performing validation, confirmation, and persistence.
     */
    private static void processTransaction() {
        System.out.println("\n--- Process Transaction ---");
        Account account = inputHandler.getAccount("Enter Account Number: ", accountManager);
        System.out.println("\nACCOUNT DETAILS");
        System.out.println("______________________");
        account.displayAccountDetails();
        System.out.println();
        System.out.println("Select Transaction Type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        System.out.println("3. Transfer");
        Transaction txn;
        String type;
        double amount;
        boolean success = false;
        int typeChoice = inputHandler.getTransactionTypeChoice("Enter choice: ");

        if (typeChoice == 3) {
            // Transfer Logic
            Account targetAccount = inputHandler.getAccount("Enter Target Account Number: ", accountManager);
            amount = inputHandler.getWithdrawalAmount("Enter Amount to Transfer: ", account);

            // Confirm transfer
            System.out.printf("Transfer $%.2f from %s to %s?\n", amount, account.getAccountNumber(),
                    targetAccount.getAccountNumber());
            String confirmationChoice = inputHandler.getStringInput("Confirm transaction? (Y/N): ");
            if (!confirmationChoice.equalsIgnoreCase("Y")) {
                System.out.println("Transaction cancelled.");
                return;
            }

            // Record transaction objects for confirmation/logging
            // Note: We calculate balances speculatively for the log.
            // If the actual transaction fails inside processTransaction, these won't be
            // saved.
            Transaction debitTxn = new Transaction(account.getAccountNumber(), "Transfer Out", amount,
                    TransactionManager.getBalanceAfter(account, amount, "Transfer Out"));
            Transaction creditTxn = new Transaction(targetAccount.getAccountNumber(), "Transfer In", amount,
                    TransactionManager.getBalanceAfter(targetAccount, amount, "Transfer In"));

            success = account.processTransaction(amount, "Transfer", targetAccount);

            if (success) {
                transactionManager.addTransaction(debitTxn);
                transactionManager.addTransaction(creditTxn);
                accountManager.saveAllData();
                System.out.println("Transaction recorded.");
            }

        } else if (typeChoice == 1) {
            type = "Deposit";
            amount = inputHandler.getDepositAmount("Enter Amount: ");
            double balanceAfter = TransactionManager.getBalanceAfter(account, amount, type);
            txn = new Transaction(account.getAccountNumber(), type, amount, balanceAfter);
            boolean isConfirmed = showTransactionConfirmationPrompt(txn);
            if (!isConfirmed)
                return;
            success = account.processTransaction(amount, type);
            if (success) {
                transactionManager.addTransaction(txn);
                accountManager.saveAllData();
                System.out.println("Transaction recorded.");
            }

        } else {
            amount = inputHandler.getWithdrawalAmount("Enter Amount: ", account);
            type = "Withdrawal";
            double balanceAfter = TransactionManager.getBalanceAfter(account, amount, type);
            txn = new Transaction(account.getAccountNumber(), type, amount, balanceAfter);
            boolean isConfirmed = showTransactionConfirmationPrompt(txn);
            if (!isConfirmed)
                return;
            success = account.processTransaction(amount, type);
            if (success) {
                transactionManager.addTransaction(txn);
                accountManager.saveAllData();
                System.out.println("Transaction recorded.");
            }
        }

        System.out.println("Press Enter to continue...");
        inputHandler.waitForEnter();
    }

    /**
     * Prompts for an account number and prints its transaction history.
     */
    private static void viewTransactionHistory() {
        Account account = inputHandler.getAccount("Enter Account Number: ", accountManager);
        System.out.println();
        account.displayAccountDetails();
        System.out.println();
        transactionManager.viewTransactionsByAccount(account.getAccountNumber());
    }

    /**
     * Prompts for an account number and generates a printable account statement.
     */
    private static void generateAccountStatements() {
        System.out.println("\nGENERATE ACCOUNT STATEMENT");
        System.out.println("_________________________________");
        Account account = inputHandler.getAccount("Enter Account Number: ", accountManager);
        transactionManager.generateStatement(account);
    }

    /**
     * Offers save/load actions so the operator can manually persist or reload data.
     */
    private static void handlePersistenceMenu() {
        System.out.println("\n=== Save / Load Data ===");
        System.out.println("1. Save data now");
        System.out.println("2. Reload data from disk");
        System.out.println("3. Back to Main Menu");
        int choice = inputHandler.getIntInput("Enter your choice: ", "Choice must be a number");
        switch (choice) {
            case 1 -> accountManager.saveAllData();
            case 2 -> accountManager.initializeData();
            default -> System.out.println("Returning to main menu.");
        }
    }

    /**
     * Executes the curated suite of unit tests and summarizes the results in the console.
     */
    private static void runTests() {
        System.out.println("\nRunning tests with JUnit ...\n");

        // Create listeners to capture test results
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        TestReportingListener reportingListener = new TestReportingListener();

        // Build the discovery request for test classes
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectClass("test.java.models.AccountDepositTest"),
                        selectClass("test.java.models.CheckingAccountWithdrawTest"),
                        selectClass("test.java.models.AccountTransferTest"),
                        selectClass("test.java.models.AccountProcessTransactionTest"),
                        selectClass("test.java.models.SavingsAccountWithdrawTest"),
                        selectClass("test.java.models.ConcurrentDepositsTest"),
                        selectClass("test.java.models.ConcurrentWithdrawalsTest"),
                        selectClass("test.java.models.ConcurrentMixedTransactionsTest"))
                .build();

        // Create and execute the launcher
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(summaryListener, reportingListener);
        launcher.execute(request);

        // Get the test execution summary
        TestExecutionSummary summary = summaryListener.getSummary();

        // Display all test results in the requested format
        reportingListener.displayResults();

        System.out.println("\n--- Summary ---");
        System.out.println("Tests Found:      " + summary.getTestsFoundCount());
        System.out.println("Tests Started:    " + summary.getTestsStartedCount());
        System.out.println("Tests Succeeded:  " + summary.getTestsSucceededCount());
        System.out.println("Tests Failed:     " + summary.getTestsFailedCount());
        System.out.println("Tests Skipped:    " + summary.getTestsSkippedCount());
        System.out.println("Tests Aborted:    " + summary.getTestsAbortedCount());

        // Display failure details if any
        if (!summary.getFailures().isEmpty()) {
            System.out.println("\n--- Failure Details ---");
            summary.getFailures().forEach(failure -> {
                System.out.println("\n✗ " + failure.getTestIdentifier().getDisplayName());
                System.out.println("  " + failure.getException().getMessage());
            });
        }

        // Overall status
        System.out.println();
        if (summary.getTestsFailedCount() == 0 && summary.getTestsAbortedCount() == 0
                && summary.getTestsFoundCount() > 0) {
            System.out.printf("✓ All %d tests passed!", summary.getTestsFoundCount());
        } else if (summary.getTestsFailedCount() > 0 || summary.getTestsAbortedCount() > 0) {
            System.out.println("✗ Some tests failed.");
        } else {
            System.out.println("No tests were executed.");
        }

        System.out.println("\nPress Enter to continue...");
        inputHandler.waitForEnter();
    }

    /**
     * Presents a submenu for concurrent transaction simulations and executes
     * the corresponding JUnit tests based on user selection.
     */
    private static void runConcurrentSimulation() {
        boolean backToMain = false;
        while (!backToMain) {
            System.out.println("\n=== Concurrent Transaction Simulation ===");
            System.out.println("1. Concurrent Deposits Test");
            System.out.println("2. Concurrent Withdrawals Test");
            System.out.println("3. Concurrent Deposits & Withdrawals Test");
            System.out.println("4. Run Visual Simulation (Legacy)");
            System.out.println("5. Back to Main Menu");

            int choice = inputHandler.getIntInput("Enter your choice: ", "Choice must be a number");

            switch (choice) {
                case 1:
                    runConcurrentDepositsTest();
                    break;
                case 2:
                    runConcurrentWithdrawalsTest();
                    break;
                case 3:
                    runConcurrentMixedTransactionsTest();
                    break;
                case 4:
                    runVisualSimulation();
                    break;
                case 5:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Runs JUnit tests for concurrent deposit operations.
     */
    private static void runConcurrentDepositsTest() {
        System.out.println("\n=== Running Concurrent Deposits Tests ===\n");
        runSpecificTestClass("test.java.models.ConcurrentDepositsTest");
    }

    /**
     * Runs JUnit tests for concurrent withdrawal operations.
     */
    private static void runConcurrentWithdrawalsTest() {
        System.out.println("\n=== Running Concurrent Withdrawals Tests ===\n");
        runSpecificTestClass("test.java.models.ConcurrentWithdrawalsTest");
    }

    /**
     * Runs JUnit tests for concurrent mixed deposit and withdrawal operations.
     */
    private static void runConcurrentMixedTransactionsTest() {
        System.out.println("\n=== Running Concurrent Mixed Transactions Tests ===\n");
        runSpecificTestClass("test.java.models.ConcurrentMixedTransactionsTest");
    }

    /**
     * Helper method to run a specific test class and display results.
     */
    private static void runSpecificTestClass(String testClassName) {
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        TestReportingListener reportingListener = new TestReportingListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClassName))
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(summaryListener, reportingListener);
        launcher.execute(request);

        TestExecutionSummary summary = summaryListener.getSummary();
        reportingListener.displayResults();

        System.out.println("\n--- Summary ---");
        System.out.println("Tests Found:      " + summary.getTestsFoundCount());
        System.out.println("Tests Started:    " + summary.getTestsStartedCount());
        System.out.println("Tests Succeeded:  " + summary.getTestsSucceededCount());
        System.out.println("Tests Failed:     " + summary.getTestsFailedCount());
        System.out.println("Tests Skipped:    " + summary.getTestsSkippedCount());
        System.out.println("Tests Aborted:    " + summary.getTestsAbortedCount());

        if (!summary.getFailures().isEmpty()) {
            System.out.println("\n--- Failure Details ---");
            summary.getFailures().forEach(failure -> {
                System.out.println("\n✗ " + failure.getTestIdentifier().getDisplayName());
                System.out.println("  " + failure.getException().getMessage());
            });
        }

        System.out.println();
        if (summary.getTestsFailedCount() == 0 && summary.getTestsAbortedCount() == 0
                && summary.getTestsFoundCount() > 0) {
            System.out.printf("✓ All %d tests passed!%n", summary.getTestsFoundCount());
        } else if (summary.getTestsFailedCount() > 0 || summary.getTestsAbortedCount() > 0) {
            System.out.println("✗ Some tests failed.");
        } else {
            System.out.println("No tests were executed.");
        }

        System.out.println("\nPress Enter to continue...");
        inputHandler.waitForEnter();
    }

    /**
     * Runs a visual simulation showing concurrent transactions in action.
     */
    private static void runVisualSimulation() {
        System.out.println("Running concurrent transaction simulation...");

        // Create deterministic test accounts for simulation
        Customer customer1 = new RegularCustomer("Alice Smith", 30, "555-0101", "alice@example.com", "123 Main St");
        Customer customer2 = new RegularCustomer("Bob Johnson", 35, "555-0102", "bob@example.com", "456 Oak Ave");

        Account primaryAccount = new CheckingAccount(customer1, 5000.0);
        Account secondaryAccount = new SavingsAccount(customer2, 3000.0);

        System.out.printf("Primary account: %s (%s) - Initial balance: $%.2f%n",
                primaryAccount.getAccountNumber(),
                primaryAccount.getCustomer().getName(),
                primaryAccount.getBalance());
        System.out.printf("Secondary account: %s (%s) - Initial balance: $%.2f%n",
                secondaryAccount.getAccountNumber(),
                secondaryAccount.getCustomer().getName(),
                secondaryAccount.getBalance());
        System.out.println();

        Object consoleLock = new Object();
        List<Thread> workers = new ArrayList<>();
        workers.add(new Thread(() -> simulateTransactions(primaryAccount, "Deposit", consoleLock, 5), "Thread-1"));
        workers.add(new Thread(() -> simulateTransactions(primaryAccount, "Withdrawal", consoleLock, 5), "Thread-2"));
        workers.add(new Thread(() -> simulateTransactions(primaryAccount, "Deposit", consoleLock, 5), "Thread-3"));
        workers.add(new Thread(() -> simulateTransactions(secondaryAccount, "Withdrawal", consoleLock, 5),
                "Thread-4"));
        workers.add(new Thread(() -> simulateTransactions(secondaryAccount, "Deposit", consoleLock, 5),
                "Thread-5"));

        workers.forEach(Thread::start);
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Simulation interrupted.");
                return;
            }
        }

        System.out.println("\nOptional parallel-stream batch demo...");
        List<Runnable> batchTasks = List.of(
                () -> runBatchTask(primaryAccount, "Deposit", 125.00, consoleLock),
                () -> runBatchTask(primaryAccount, "Withdrawal", 60.00, consoleLock),
                () -> runBatchTask(secondaryAccount, "Deposit", 85.00, consoleLock),
                () -> runBatchTask(secondaryAccount, "Withdrawal", 40.00, consoleLock));
        batchTasks.parallelStream().forEach(Runnable::run);

        System.out.println("\n✓ Thread-safe operations completed successfully");
        System.out.println("Final balances after simulation:");
        System.out.printf("%s -> $%.2f%n", primaryAccount.getAccountNumber(), primaryAccount.getBalance());
        System.out.printf("%s -> $%.2f%n", secondaryAccount.getAccountNumber(), secondaryAccount.getBalance());
        System.out.println("Press Enter to return to the menu...");
        inputHandler.waitForEnter();
    }

    private static void simulateTransactions(Account account, String type, Object consoleLock, int iterations) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < iterations; i++) {
            double min = type.equalsIgnoreCase("Deposit") ? 25.0 : 15.0;
            double max = type.equalsIgnoreCase("Deposit") ? 200.0 : 120.0;
            double amount = Math.round(random.nextDouble(min, max) * 100.0) / 100.0;

            // Synchronize to ensure we get the balance immediately after the transaction
            boolean success;
            double balance;
            synchronized (account) {
                success = account.processTransaction(amount, type);
                balance = account.getBalance();
                // While processTransaction() and getBalance() are individually thread-safe,
                // there's a race condition between these two method calls
                // another thread could modify the balance after processTransaction() but before getBalance().
            }

            synchronized (consoleLock) {
                String verb = type.equalsIgnoreCase("Deposit") ? "depositing" : "withdrawing";
                System.out.printf("[%s] %s %s $%.2f -> balance $%.2f (%s)%n",
                        Thread.currentThread().getName(),
                        account.getAccountNumber(),
                        verb,
                        amount,
                        balance,
                        success ? "ok" : "rejected");
            }
            try {
                Thread.sleep(random.nextInt(40, 120));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void runBatchTask(Account account, String type, double amount, Object consoleLock) {
        // Synchronize to ensure we get the balance immediately after the transaction
        boolean success;
        double balance;
        synchronized (account) {
            success = account.processTransaction(amount, type);
            balance = account.getBalance();
        }

        synchronized (consoleLock) {
            String verb = type.equalsIgnoreCase("Deposit") ? "deposited" : "withdrew";
            System.out.printf("[parallel-%s] %s %s $%.2f -> balance $%.2f (%s)%n",
                    Thread.currentThread().getName(),
                    account.getAccountNumber(),
                    verb,
                    amount,
                    balance,
                    success ? "ok" : "rejected");
        }
    }

    /**
     * Captures per-test results so they can be printed in the desired format after execution.
     */
    private static class TestReportingListener implements TestExecutionListener {
        private final java.util.List<TestResult> results = new java.util.ArrayList<>();

        /**
         * Records the outcome of each individual test case for later reporting.
         */
        @Override
        public void executionFinished(org.junit.platform.launcher.TestIdentifier testIdentifier,
                TestExecutionResult result) {
            // Capture test methods only (not test containers/classes)
            if (testIdentifier.isTest()) {
                String testName = testIdentifier.getDisplayName();
                String status = result.getStatus().toString();
                results.add(new TestResult(testName, status));
            }
        }

        /**
         * Prints the collected test results, formatting each case on its own line.
         */
        void displayResults() {
            if (results.isEmpty()) {
                return;
            }
            for (TestResult result : results) {
                String status = result.status.equals("SUCCESSFUL") ? "PASSED" : "FAILED";
                System.out.printf("Test: %s ........%s%n", result.testName, status);
            }
        }

        /**
         * Lightweight value object for storing a test name and its status.
         */
        private static class TestResult {
            String testName;
            String status;

            TestResult(String testName, String status) {
                this.testName = testName;
                this.status = status;
            }
        }
    }
}
