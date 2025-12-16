# 🏦 Bank Account Management System Project

**Complexity:** Medium 
**Time Estimate:** 10 hours 

---

## 🎯 Objectives

By completing this project, you will be able to:
* Apply OOP principles (encapsulation, inheritance, polymorphism) to design Java classes and interfaces for real-world problems.
* Create well-structured applications integrating primitive data types, control structures, and custom objects.
* Analyze class relationships to choose between inheritance, composition, abstract classes, and interfaces.
* Evaluate code quality using proper encapsulation, naming conventions, and OOP best practices.
* Apply polymorphic behavior with method overriding to build flexible, extensible code.
* Apply fundamental Data Structures and Algorithms concepts to manage, search, and organize account and transaction data efficiently.

---

## 💻 What You'll Build

A console application with the following core features and account/customer types: 

### Core Features
* **Create Account** - Register new bank accounts for customers.
* **View Accounts** - Display all accounts with their details.
* **Process Transaction** - Deposit or withdraw money from accounts.
* **View Transactions** - Display transaction history for an account.
* **Simple Menu** - Navigate through options.

### Account Types
| Account Type | Key Features | Parameters |
| :--- | :--- | :--- |
| **Savings Account** | Earns interest  | Interest Rate: 3.5% annually, Minimum Balance: \$500  |
| **Checking Account** | No interest, has overdraft limit  | Overdraft Limit: \$1000, Monthly Fee: \$10  |

### Customer Types
| Customer Type | Key Features | Benefits/Constraints |
| :--- | :--- | :--- |
| **Regular Customer** | Standard banking services  | Standard banking services  |
| **Premium Customer** | Higher transaction limits, waived fees  | Minimum Balance: \$10,000, Benefits: No monthly fees, Priority service  |

---

## ✅ User Stories and Acceptance Criteria

| ID | User Story | Acceptance Criteria |
| :--- | :--- | :--- |
| **US-1** | As a bank staff member, I want to view all bank accounts so that I can see account details and balances. | Display minimum 5 accounts (3 Savings, 2 Checking). Show account number, customer name, type, balance, and status. Savings accounts show interest rate and minimum balance. Checking accounts show overdraft limit and monthly fee. Display total accounts and total bank balance. |
| **US-2** | As a bank staff member, I want to create new bank accounts so that customers can start banking. | Capture customer details (name, age, contact, address). Support two customer types (Regular/Premium) and two account types (Savings/Checking). Premium customers have waived monthly fees. Auto-generate unique account number. Require initial deposit. Display confirmation with all details. |
| **US-3** | As a bank staff member, I want to process deposits and withdrawals so that customers can access their money. | User enters and validates account number. Allow selection of transaction type (deposit/withdrawal). Deposits: accept any positive amount. Withdrawals: check sufficient balance (or overdraft for checking). Savings withdrawals: ensure minimum balance is maintained. Generate unique transaction ID. Update account balance. Show confirmation before finalizing. |
| **US-4** | As a bank staff member, I want to view transaction history for an account so that I can track account activity. | Display all transactions for a specific account. Show ID, date/time, type, amount, and balance after transaction. Display summary: total deposits, total withdrawals, net change. Handle accounts with no transactions. Transactions displayed in reverse chronological order (newest first). |
| **US-5** | As a user, I want to navigate through menu options so that I can use all features. | Display clear menu with 5 options. Accept and validate user input. Execute selected option. Loop until user exits. Handle invalid input gracefully. |

---

## 🧱 Classes to Create (11 Total)

The project requires 11 classes, including abstract classes and an interface.

### Account Hierarchy
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Account** | Abstract Class  | `private` fields: `accountNumber`, `customer`, `balance`, `status`. `static` field: `accountCounter`. Abstract methods: `displayAccountDetails()`, `getAccountType()`. Methods: `deposit(double amount)`, `withdraw(double amount)`. |
| **SavingsAccount** | Extends `Account`  | `private` fields: `interestRate` (3.5%), `minimumBalance` (\$500). Overrides: `displayAccountDetails()`, `getAccountType()` ("Savings"), `withdraw()` (checks minimum balance). Method: `calculateInterest()`. |
| **CheckingAccount** | Extends `Account`  | `private` fields: `overdraftLimit` (\$1000), `monthlyFee` (\$10). Overrides: `displayAccountDetails()`, `getAccountType()` ("Checking"), `withdraw()` (allows overdraft up to limit). Method: `applyMonthlyFee()`. |

### Customer Hierarchy
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Customer** | Abstract Class  | `private` fields: `customerId`, `name`, `age`, `contact`, `address`. `static` field: `customerCounter`. Abstract methods: `displayCustomerDetails()`, `getCustomerType()`. |
| **RegularCustomer** | Extends `Customer`  | Overrides: `displayCustomerDetails()`, `getCustomerType()` ("Regular"). |
| **PremiumCustomer** | Extends `Customer`  | `private` field: `minimumBalance` (\$10,000). Overrides: `displayCustomerDetails()`, `getCustomerType()` ("Premium"). Method: `hasWaivedFees()` (returns true). |

### Management & Utility
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Transactable** | Interface  | Method: `processTransaction(double amount, String type)` returns `boolean`. |
| **Transaction** | Regular Class  | `static` field: `transactionCounter`. `private` fields: `transactionId`, `accountNumber`, `type`, `amount`, `balanceAfter`, `timestamp`. Auto-generates ID (TXN001, etc.) and timestamp. Method: `displayTransactionDetails()`. |
| **AccountManager** | Uses Composition  | `private` fields: `accounts` (Account array, size 50), `accountCount`. Methods: `addAccount()`, `findAccount()`, `viewAllAccounts()`, `getTotalBalance()`, `getAccountCount()`. |
| **TransactionManager** | Uses Composition  | `private` fields: `transactions` (Transaction array, size 200), `transactionCount`. Methods: `addTransaction()`, `viewTransactionsByAccount()`, `calculateTotalDeposits()`, `calculateTotalWithdrawals()`, `getTransactionCount()`. |
| **Main** | Main Entry Class  | Contains the primary execution logic and menu navigation. |

---


## ⚙️ Minimum Requirements & Constraints

* All 11 required classes must be implemented.
* All 5 user stories must be fully working.
* Static counters must correctly generate unique IDs (ACC001, TXN001, etc.).
* Use appropriate data structures (e.g., arrays or lists) for account and transaction management.
* Input validation must be implemented.
* Minimum balance must be enforced for savings accounts.
* Overdraft limit must be enforced for checking accounts.
* A `README.md` must be included.
* GitHub repository must be public and the link submitted.

---

Would you like to see a summary of the project's grading rubric or the detailed testing scenarios?