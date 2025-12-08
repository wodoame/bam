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

import java.util.Scanner;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class Main {
    private static final InputHandler inputHandler = new InputHandler();
    private static final AccountManager accountManager = new AccountManager(inputHandler);
    private static final TransactionManager transactionManager = new TransactionManager();

    public static void main(String[] args) {
        accountManager.generateSeedAccounts(transactionManager);
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
                    runTests();
                    break;
                case 5:
                    exit = true;
                    System.out.println("\nThank you for using the Bank Account Management System!\nAll data saved in memory. Remember to commit your latest changest to Git!\nGoodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        inputHandler.closeScanner();
    }

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

    private static void printMainMenu() {
        System.out.println("\n=== Bank Account Management System ===");
        System.out.println("1. Manage Accounts");
        System.out.println("2. Perform Transactions");
        System.out.println("3. Generate Account Statements");
        System.out.println("4. Run tests");
        System.out.println("5. Exit");
    }

    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.println("Select Customer Type:");
        System.out.println("1. Regular (Standard banking services)");
        System.out.println("2. Premium (Enhanced benefits, min balance $10,000)");
        int customerTypeChoice = inputHandler.getCustomerTypeChoice("Enter choice: ");
        String name = inputHandler.getName("Enter Customer Name: ");
        int age = inputHandler.getAge("Enter Age: ");
        String contact = inputHandler.getContact("Enter Contact Number: ");
        String address = inputHandler.getStringInput("Enter Address: ");

        Customer customer;
        if (customerTypeChoice == 1) {
            customer = new RegularCustomer(name, age, contact, address);
        } else {
            customer = new PremiumCustomer(name, age, contact, address);
        }

        System.out.println("Select Account Type:");
        System.out.printf("1. Savings (Interest Rate: %.1f, Minimum Balance: $%.2f)\n", SavingsAccount.INTEREST_RATE, SavingsAccount.MINIMUM_BALANCE);
        System.out.printf("2. Checking (Overdraft: $%.2f, Monthly Fee: $%.2f)\n", CheckingAccount.OVERDRAFT_LIMIT, CheckingAccount.MONTHLY_FEE );
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
        Transaction txn;
        String type;
        double amount;
        boolean success;
        int typeChoice = inputHandler.getTransactionTypeChoice("Enter choice: ");
        if (typeChoice == 1) {
            type = "Deposit";
            amount = inputHandler.getDepositAmount("Enter Amount: ");
            txn = new Transaction(account.getAccountNumber(), type, amount, account.getBalance() + amount);

        } else {
            amount = inputHandler.getWithdrawalAmount("Enter Amount: ", account);
            type = "Withdrawal";
            String accountType = account.getAccountType();
            if(accountType.equalsIgnoreCase("savings")){
                txn = new Transaction(account.getAccountNumber(), type, amount, account.getBalance() - amount);
            }
            else{
                // checking account with overdraft
                double balanceAfter = account.getBalance() + CheckingAccount.OVERDRAFT_LIMIT - amount;
                txn = new Transaction(account.getAccountNumber(), type, amount, balanceAfter);
            }
        }
        boolean isConfirmed = showTransactionConfirmationPrompt(txn);
        if (!isConfirmed) {
            return;
        }
        success = account.processTransaction(amount, type);

        if (success) {
            transactionManager.addTransaction(txn);
            System.out.println("Transaction recorded.");
        }
        System.out.println("Press Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    private static void viewTransactionHistory() {
        Account account = inputHandler.getAccount("Enter Account Number: ", accountManager);
        System.out.println();
        account.displayAccountDetails();
        System.out.println();
        transactionManager.viewTransactionsByAccount(account.getAccountNumber());
    }

    private static void generateAccountStatements() {
        System.out.println("\nGENERATE ACCOUNT STATEMENT");
        System.out.println("_________________________________");
        Account account = inputHandler.getAccount("Enter Account Number: ", accountManager);
        transactionManager.generateStatement(account);
    }

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
                        selectClass("test.java.models.SavingsAccountWithdrawTest")
                )
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
        if (summary.getTestsFailedCount() == 0 && summary.getTestsAbortedCount() == 0 && summary.getTestsFoundCount() > 0) {
            System.out.printf("✓ All %d tests passed!", summary.getTestsFoundCount());
        } else if (summary.getTestsFailedCount() > 0 || summary.getTestsAbortedCount() > 0) {
            System.out.println("✗ Some tests failed.");
        } else {
            System.out.println("No tests were executed.");
        }

        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    // Custom TestExecutionListener to capture and report all test results
    private static class TestReportingListener implements TestExecutionListener {
        private final java.util.List<TestResult> results = new java.util.ArrayList<>();

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

        void displayResults() {
            if (results.isEmpty()) {
                return;
            }
            for (TestResult result : results) {
                String status = result.status.equals("SUCCESSFUL") ? "PASSED" : "FAILED";
                System.out.printf("Test: %s ........%s%n", result.testName, status);
            }
        }

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
