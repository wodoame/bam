# 🏦 Bank Account Management System Project

[cite_start]**Complexity:** Medium [cite: 4]
[cite_start]**Time Estimate:** 10 hours [cite: 5]

---

## 🎯 Objectives

By completing this project, you will be able to:
* [cite_start]Apply OOP principles (encapsulation, inheritance, polymorphism) to design Java classes and interfaces for real-world problems[cite: 8].
* [cite_start]Create well-structured applications integrating primitive data types, control structures, and custom objects[cite: 9].
* [cite_start]Analyze class relationships to choose between inheritance, composition, abstract classes, and interfaces[cite: 10].
* [cite_start]Evaluate code quality using proper encapsulation, naming conventions, and OOP best practices[cite: 10].
* [cite_start]Apply polymorphic behavior with method overriding to build flexible, extensible code[cite: 11].
* [cite_start]Apply fundamental Data Structures and Algorithms concepts to manage, search, and organize account and transaction data efficiently[cite: 12].

---

## 💻 What You'll Build

[cite_start]A console application with the following core features and account/customer types: [cite: 14]

### Core Features
* [cite_start]**Create Account** - Register new bank accounts for customers[cite: 16].
* [cite_start]**View Accounts** - Display all accounts with their details[cite: 17].
* [cite_start]**Process Transaction** - Deposit or withdraw money from accounts[cite: 18].
* [cite_start]**View Transactions** - Display transaction history for an account[cite: 19].
* [cite_start]**Simple Menu** - Navigate through options[cite: 20].

### Account Types
| Account Type | Key Features | Parameters |
| :--- | :--- | :--- |
| **Savings Account** | [cite_start]Earns interest [cite: 22] | [cite_start]Interest Rate: 3.5% annually, Minimum Balance: \$500 [cite: 22] |
| **Checking Account** | [cite_start]No interest, has overdraft limit [cite: 23] | [cite_start]Overdraft Limit: \$1000, Monthly Fee: \$10 [cite: 23] |

### Customer Types
| Customer Type | Key Features | Benefits/Constraints |
| :--- | :--- | :--- |
| **Regular Customer** | [cite_start]Standard banking services [cite: 25] | [cite_start]Standard banking services [cite: 25] |
| **Premium Customer** | [cite_start]Higher transaction limits, waived fees [cite: 26] | [cite_start]Minimum Balance: \$10,000, Benefits: No monthly fees, Priority service [cite: 26] |

---

## ✅ User Stories and Acceptance Criteria

| ID | User Story | Acceptance Criteria |
| :--- | :--- | :--- |
| **US-1** | [cite_start]As a bank staff member, I want to view all bank accounts so that I can see account details and balances[cite: 327, 328, 329]. | [cite_start]Display minimum 5 accounts (3 Savings, 2 Checking)[cite: 331]. [cite_start]Show account number, customer name, type, balance, and status[cite: 332]. [cite_start]Savings accounts show interest rate and minimum balance[cite: 333]. [cite_start]Checking accounts show overdraft limit and monthly fee[cite: 334]. [cite_start]Display total accounts and total bank balance[cite: 335]. |
| **US-2** | [cite_start]As a bank staff member, I want to create new bank accounts so that customers can start banking[cite: 357, 358, 359]. | [cite_start]Capture customer details (name, age, contact, address)[cite: 361]. [cite_start]Support two customer types (Regular/Premium) and two account types (Savings/Checking)[cite: 361, 362]. [cite_start]Premium customers have waived monthly fees[cite: 363]. [cite_start]Auto-generate unique account number[cite: 364]. [cite_start]Require initial deposit[cite: 365]. [cite_start]Display confirmation with all details[cite: 366]. |
| **US-3** | [cite_start]As a bank staff member, I want to process deposits and withdrawals so that customers can access their money[cite: 383, 384, 385]. | [cite_start]User enters and validates account number[cite: 387, 388]. [cite_start]Allow selection of transaction type (deposit/withdrawal)[cite: 389]. [cite_start]Deposits: accept any positive amount[cite: 390]. [cite_start]Withdrawals: check sufficient balance (or overdraft for checking)[cite: 391]. [cite_start]Savings withdrawals: ensure minimum balance is maintained[cite: 392]. [cite_start]Generate unique transaction ID[cite: 393]. [cite_start]Update account balance[cite: 394]. [cite_start]Show confirmation before finalizing[cite: 395]. |
| **US-4** | [cite_start]As a bank staff member, I want to view transaction history for an account so that I can track account activity[cite: 419, 420, 421]. | [cite_start]Display all transactions for a specific account[cite: 423]. [cite_start]Show ID, date/time, type, amount, and balance after transaction[cite: 424]. [cite_start]Display summary: total deposits, total withdrawals, net change[cite: 425]. [cite_start]Handle accounts with no transactions[cite: 426]. [cite_start]Transactions displayed in reverse chronological order (newest first)[cite: 427]. |
| **US-5** | [cite_start]As a user, I want to navigate through menu options so that I can use all features[cite: 439, 440, 441]. | [cite_start]Display clear menu with 5 options[cite: 443]. [cite_start]Accept and validate user input[cite: 444]. [cite_start]Execute selected option[cite: 445]. [cite_start]Loop until user exits[cite: 446]. [cite_start]Handle invalid input gracefully[cite: 447]. |

---

## 🧱 Classes to Create (11 Total)

[cite_start]The project requires 11 classes, including abstract classes and an interface[cite: 460].

### Account Hierarchy
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Account** | [cite_start]Abstract Class [cite: 337] | [cite_start]`private` fields: `accountNumber`, `customer`, `balance`, `status`[cite: 338]. [cite_start]`static` field: `accountCounter`[cite: 338]. [cite_start]Abstract methods: `displayAccountDetails()`, `getAccountType()`[cite: 340, 341]. [cite_start]Methods: `deposit(double amount)`, `withdraw(double amount)`[cite: 342]. |
| **SavingsAccount** | [cite_start]Extends `Account` [cite: 343] | [cite_start]`private` fields: `interestRate` (3.5%), `minimumBalance` (\$500)[cite: 344]. [cite_start]Overrides: `displayAccountDetails()`, `getAccountType()` ("Savings"), `withdraw()` (checks minimum balance)[cite: 345, 346, 347]. [cite_start]Method: `calculateInterest()`[cite: 348]. |
| **CheckingAccount** | [cite_start]Extends `Account` [cite: 349] | [cite_start]`private` fields: `overdraftLimit` (\$1000), `monthlyFee` (\$10)[cite: 350, 351]. [cite_start]Overrides: `displayAccountDetails()`, `getAccountType()` ("Checking"), `withdraw()` (allows overdraft up to limit)[cite: 352, 353, 354]. [cite_start]Method: `applyMonthlyFee()`[cite: 355]. |

### Customer Hierarchy
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Customer** | [cite_start]Abstract Class [cite: 368] | [cite_start]`private` fields: `customerId`, `name`, `age`, `contact`, `address`[cite: 369]. [cite_start]`static` field: `customerCounter`[cite: 369]. [cite_start]Abstract methods: `displayCustomerDetails()`, `getCustomerType()`[cite: 371, 372]. |
| **RegularCustomer** | [cite_start]Extends `Customer` [cite: 373] | [cite_start]Overrides: `displayCustomerDetails()`, `getCustomerType()` ("Regular")[cite: 375, 376]. |
| **PremiumCustomer** | [cite_start]Extends `Customer` [cite: 377] | [cite_start]`private` field: `minimumBalance` (\$10,000)[cite: 378]. [cite_start]Overrides: `displayCustomerDetails()`, `getCustomerType()` ("Premium")[cite: 380]. [cite_start]Method: `hasWaivedFees()` (returns true)[cite: 381]. |

### Management & Utility
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Transactable** | [cite_start]Interface [cite: 397] | [cite_start]Method: `processTransaction(double amount, String type)` returns `boolean`[cite: 398]. |
| **Transaction** | [cite_start]Regular Class [cite: 399] | [cite_start]`static` field: `transactionCounter`[cite: 400]. [cite_start]`private` fields: `transactionId`, `accountNumber`, `type`, `amount`, `balanceAfter`, `timestamp`[cite: 401]. [cite_start]Auto-generates ID (TXN001, etc.) and timestamp[cite: 403, 404]. [cite_start]Method: `displayTransactionDetails()`[cite: 406]. |
| **AccountManager** | [cite_start]Uses Composition [cite: 407] | [cite_start]`private` fields: `accounts` (Account array, size 50), `accountCount`[cite: 408, 409]. [cite_start]Methods: `addAccount()`, `findAccount()`, `viewAllAccounts()`, `getTotalBalance()`, `getAccountCount()`[cite: 411, 412, 413, 414, 416]. |
| **TransactionManager** | [cite_start]Uses Composition [cite: 429] | [cite_start]`private` fields: `transactions` (Transaction array, size 200), `transactionCount`[cite: 430, 431]. [cite_start]Methods: `addTransaction()`, `viewTransactionsByAccount()`, `calculateTotalDeposits()`, `calculateTotalWithdrawals()`, `getTransactionCount()`[cite: 433, 434, 435, 436, 437]. |
| **Main** | [cite_start]Main Entry Class [cite: 465] # 🏦 Bank Account Management System Project

[cite_start]**Complexity:** Medium [cite: 4]
[cite_start]**Time Estimate:** 10 hours [cite: 5]

---

## 🎯 Objectives

By completing this project, you will be able to:
* [cite_start]Apply OOP principles (encapsulation, inheritance, polymorphism) to design Java classes and interfaces for real-world problems[cite: 8].
* [cite_start]Create well-structured applications integrating primitive data types, control structures, and custom objects[cite: 9].
* [cite_start]Analyze class relationships to choose between inheritance, composition, abstract classes, and interfaces[cite: 10].
* [cite_start]Evaluate code quality using proper encapsulation, naming conventions, and OOP best practices[cite: 10].
* [cite_start]Apply polymorphic behavior with method overriding to build flexible, extensible code[cite: 11].
* [cite_start]Apply fundamental Data Structures and Algorithms concepts to manage, search, and organize account and transaction data efficiently[cite: 12].

---

## 💻 What You'll Build

[cite_start]A console application with the following core features and account/customer types: [cite: 14]

### Core Features
* [cite_start]**Create Account** - Register new bank accounts for customers[cite: 16].
* [cite_start]**View Accounts** - Display all accounts with their details[cite: 17].
* [cite_start]**Process Transaction** - Deposit or withdraw money from accounts[cite: 18].
* [cite_start]**View Transactions** - Display transaction history for an account[cite: 19].
* [cite_start]**Simple Menu** - Navigate through options[cite: 20].

### Account Types
| Account Type | Key Features | Parameters |
| :--- | :--- | :--- |
| **Savings Account** | [cite_start]Earns interest [cite: 22] | [cite_start]Interest Rate: 3.5% annually, Minimum Balance: \$500 [cite: 22] |
| **Checking Account** | [cite_start]No interest, has overdraft limit [cite: 23] | [cite_start]Overdraft Limit: \$1000, Monthly Fee: \$10 [cite: 23] |

### Customer Types
| Customer Type | Key Features | Benefits/Constraints |
| :--- | :--- | :--- |
| **Regular Customer** | [cite_start]Standard banking services [cite: 25] | [cite_start]Standard banking services [cite: 25] |
| **Premium Customer** | [cite_start]Higher transaction limits, waived fees [cite: 26] | [cite_start]Minimum Balance: \$10,000, Benefits: No monthly fees, Priority service [cite: 26] |

---

## ✅ User Stories and Acceptance Criteria

| ID | User Story | Acceptance Criteria |
| :--- | :--- | :--- |
| **US-1** | [cite_start]As a bank staff member, I want to view all bank accounts so that I can see account details and balances[cite: 327, 328, 329]. | [cite_start]Display minimum 5 accounts (3 Savings, 2 Checking)[cite: 331]. [cite_start]Show account number, customer name, type, balance, and status[cite: 332]. [cite_start]Savings accounts show interest rate and minimum balance[cite: 333]. [cite_start]Checking accounts show overdraft limit and monthly fee[cite: 334]. [cite_start]Display total accounts and total bank balance[cite: 335]. |
| **US-2** | [cite_start]As a bank staff member, I want to create new bank accounts so that customers can start banking[cite: 357, 358, 359]. | [cite_start]Capture customer details (name, age, contact, address)[cite: 361]. [cite_start]Support two customer types (Regular/Premium) and two account types (Savings/Checking)[cite: 361, 362]. [cite_start]Premium customers have waived monthly fees[cite: 363]. [cite_start]Auto-generate unique account number[cite: 364]. [cite_start]Require initial deposit[cite: 365]. [cite_start]Display confirmation with all details[cite: 366]. |
| **US-3** | [cite_start]As a bank staff member, I want to process deposits and withdrawals so that customers can access their money[cite: 383, 384, 385]. | [cite_start]User enters and validates account number[cite: 387, 388]. [cite_start]Allow selection of transaction type (deposit/withdrawal)[cite: 389]. [cite_start]Deposits: accept any positive amount[cite: 390]. [cite_start]Withdrawals: check sufficient balance (or overdraft for checking)[cite: 391]. [cite_start]Savings withdrawals: ensure minimum balance is maintained[cite: 392]. [cite_start]Generate unique transaction ID[cite: 393]. [cite_start]Update account balance[cite: 394]. [cite_start]Show confirmation before finalizing[cite: 395]. |
| **US-4** | [cite_start]As a bank staff member, I want to view transaction history for an account so that I can track account activity[cite: 419, 420, 421]. | [cite_start]Display all transactions for a specific account[cite: 423]. [cite_start]Show ID, date/time, type, amount, and balance after transaction[cite: 424]. [cite_start]Display summary: total deposits, total withdrawals, net change[cite: 425]. [cite_start]Handle accounts with no transactions[cite: 426]. [cite_start]Transactions displayed in reverse chronological order (newest first)[cite: 427]. |
| **US-5** | [cite_start]As a user, I want to navigate through menu options so that I can use all features[cite: 439, 440, 441]. | [cite_start]Display clear menu with 5 options[cite: 443]. [cite_start]Accept and validate user input[cite: 444]. [cite_start]Execute selected option[cite: 445]. [cite_start]Loop until user exits[cite: 446]. [cite_start]Handle invalid input gracefully[cite: 447]. |

---

## 🧱 Classes to Create (11 Total)

[cite_start]The project requires 11 classes, including abstract classes and an interface[cite: 460].

### Account Hierarchy
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Account** | [cite_start]Abstract Class [cite: 337] | [cite_start]`private` fields: `accountNumber`, `customer`, `balance`, `status`[cite: 338]. [cite_start]`static` field: `accountCounter`[cite: 338]. [cite_start]Abstract methods: `displayAccountDetails()`, `getAccountType()`[cite: 340, 341]. [cite_start]Methods: `deposit(double amount)`, `withdraw(double amount)`[cite: 342]. |
| **SavingsAccount** | [cite_start]Extends `Account` [cite: 343] | [cite_start]`private` fields: `interestRate` (3.5%), `minimumBalance` (\$500)[cite: 344]. [cite_start]Overrides: `displayAccountDetails()`, `getAccountType()` ("Savings"), `withdraw()` (checks minimum balance)[cite: 345, 346, 347]. [cite_start]Method: `calculateInterest()`[cite: 348]. |
| **CheckingAccount** | [cite_start]Extends `Account` [cite: 349] | [cite_start]`private` fields: `overdraftLimit` (\$1000), `monthlyFee` (\$10)[cite: 350, 351]. [cite_start]Overrides: `displayAccountDetails()`, `getAccountType()` ("Checking"), `withdraw()` (allows overdraft up to limit)[cite: 352, 353, 354]. [cite_start]Method: `applyMonthlyFee()`[cite: 355]. |

### Customer Hierarchy
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Customer** | [cite_start]Abstract Class [cite: 368] | [cite_start]`private` fields: `customerId`, `name`, `age`, `contact`, `address`[cite: 369]. [cite_start]`static` field: `customerCounter`[cite: 369]. [cite_start]Abstract methods: `displayCustomerDetails()`, `getCustomerType()`[cite: 371, 372]. |
| **RegularCustomer** | [cite_start]Extends `Customer` [cite: 373] | [cite_start]Overrides: `displayCustomerDetails()`, `getCustomerType()` ("Regular")[cite: 375, 376]. |
| **PremiumCustomer** | [cite_start]Extends `Customer` [cite: 377] | [cite_start]`private` field: `minimumBalance` (\$10,000)[cite: 378]. [cite_start]Overrides: `displayCustomerDetails()`, `getCustomerType()` ("Premium")[cite: 380]. [cite_start]Method: `hasWaivedFees()` (returns true)[cite: 381]. |

### Management & Utility
| Class | Type | Key Fields/Methods |
| :--- | :--- | :--- |
| **Transactable** | [cite_start]Interface [cite: 397] | [cite_start]Method: `processTransaction(double amount, String type)` returns `boolean`[cite: 398]. |
| **Transaction** | [cite_start]Regular Class [cite: 399] | [cite_start]`static` field: `transactionCounter`[cite: 400]. [cite_start]`private` fields: `transactionId`, `accountNumber`, `type`, `amount`, `balanceAfter`, `timestamp`[cite: 401]. [cite_start]Auto-generates ID (TXN001, etc.) and timestamp[cite: 403, 404]. [cite_start]Method: `displayTransactionDetails()`[cite: 406]. |
| **AccountManager** | [cite_start]Uses Composition [cite: 407] | [cite_start]`private` fields: `accounts` (Account array, size 50), `accountCount`[cite: 408, 409]. [cite_start]Methods: `addAccount()`, `findAccount()`, `viewAllAccounts()`, `getTotalBalance()`, `getAccountCount()`[cite: 411, 412, 413, 414, 416]. |
| **TransactionManager** | [cite_start]Uses Composition [cite: 429] | [cite_start]`private` fields: `transactions` (Transaction array, size 200), `transactionCount`[cite: 430, 431]. [cite_start]Methods: `addTransaction()`, `viewTransactionsByAccount()`, `calculateTotalDeposits()`, `calculateTotalWithdrawals()`, `getTransactionCount()`[cite: 433, 434, 435, 436, 437]. |
| **Main** | [cite_start]Main Entry Class [cite: 465] | Contains the primary execution logic and menu navigation. |

---

## ⚙️ Minimum Requirements & Constraints

* [cite_start]All 11 required classes must be implemented[cite: 449].
* [cite_start]All 5 user stories must be fully working[cite: 450].
* [cite_start]Static counters must correctly generate unique IDs (ACC001, TXN001, etc.)[cite: 451].
* [cite_start]Use appropriate data structures (e.g., arrays or lists) for account and transaction management[cite: 452].
* [cite_start]Input validation must be implemented[cite: 453].
* [cite_start]Minimum balance must be enforced for savings accounts[cite: 455].
* [cite_start]Overdraft limit must be enforced for checking accounts[cite: 456].
* [cite_start]A `README.md` must be included[cite: 457].
* [cite_start]GitHub repository must be public and the link submitted[cite: 458, 459].

---

Would you like to see a summary of the project's grading rubric or the detailed testing scenarios?| Contains the primary execution logic and menu navigation. |

---

## ⚙️ Minimum Requirements & Constraints

* [cite_start]All 11 required classes must be implemented[cite: 449].
* [cite_start]All 5 user stories must be fully working[cite: 450].
* [cite_start]Static counters must correctly generate unique IDs (ACC001, TXN001, etc.)[cite: 451].
* [cite_start]Use appropriate data structures (e.g., arrays or lists) for account and transaction management[cite: 452].
* [cite_start]Input validation must be implemented[cite: 453].
* [cite_start]Minimum balance must be enforced for savings accounts[cite: 455].
* [cite_start]Overdraft limit must be enforced for checking accounts[cite: 456].
* [cite_start]A `README.md` must be included[cite: 457].
* [cite_start]GitHub repository must be public and the link submitted[cite: 458, 459].

---

Would you like to see a summary of the project's grading rubric or the detailed testing scenarios?