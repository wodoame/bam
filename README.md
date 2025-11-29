# Bank Account Management System

A console-based banking application developed in Java.

## Features
- **Account Management**: Create Savings and Checking accounts.
- **Customer Management**: Support for Regular and Premium customers.
- **Transaction Processing**: Deposit and withdraw funds with validation.
- **Transaction History**: View detailed transaction logs for any account.
- **Data Persistence**: In-memory storage using arrays (as per requirements).

## Project Structure
- `src/com/bam`: Source code.
  - `Main.java`: Entry point and menu system.
  - `AccountManager.java`: Manages accounts.
  - `TransactionManager.java`: Manages transactions.
  - `Account.java`, `SavingsAccount.java`, `CheckingAccount.java`: Account hierarchy.
  - `Customer.java`, `RegularCustomer.java`, `PremiumCustomer.java`: Customer hierarchy.
  - `Transaction.java`: Transaction record.
  - `Transactable.java`: Interface for transactions.

## How to Run
1. Compile the code:
   ```bash
   javac -d out src/com/bam/*.java
   ```
2. Run the application:
   ```bash
   java -cp out com.bam.Main
   ```

## Requirements Met
- [x] All 11 required classes implemented.
- [x] All 5 user stories working.
- [x] Unique ID generation.
- [x] Input validation.
- [x] Minimum balance and overdraft enforcement.
