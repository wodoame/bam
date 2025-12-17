# 🏦 Bank Account Management System

A comprehensive console-based banking application developed in Java, demonstrating modern software engineering practices including OOP principles, exception handling, unit testing, functional programming, file persistence, and thread-safe concurrent operations.

[![Java Version](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue)](https://maven.apache.org/)
[![JUnit](https://img.shields.io/badge/JUnit-5.10.1-green)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## 📋 Table of Contents
- [Features](#-features)
- [System Requirements](#-system-requirements)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Usage Guide](#-usage-guide)
- [Architecture](#-architecture)
- [Testing](#-testing)
- [Data Persistence](#-data-persistence)
- [Concurrency Support](#-concurrency-support)
- [Development Workflow](#-development-workflow)
- [Requirements Checklist](#-requirements-checklist)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### Core Banking Operations
- **Account Management**: Create and manage Savings and Checking accounts
- **Customer Management**: Support for Regular and Premium customer types
- **Transaction Processing**: Deposit, withdrawal, and transfer operations with comprehensive validation
- **Transaction History**: Detailed transaction logs with filtering and sorting capabilities
- **Account Statements**: Generate formatted account statements with transaction summaries

### Advanced Features
- **Custom Exception Handling**: Robust error handling with custom exceptions for invalid operations
- **File Persistence**: Save and load account/transaction data using Java NIO
- **Thread-Safe Operations**: Concurrent transaction processing with synchronized methods
- **Functional Programming**: Stream API for data processing and filtering
- **Regex Validation**: Input validation for emails, phone numbers, and account numbers
- **JUnit Testing**: Comprehensive test suite including concurrency tests

### Account Types
| Account Type | Interest Rate | Minimum Balance | Overdraft Limit | Monthly Fee |
|-------------|---------------|-----------------|-----------------|-------------|
| **Savings Account** | 3.5% | $500 | N/A | $0 |
| **Checking Account** | 0% | N/A | $1,000 | $10 |

### Customer Types
| Customer Type | Minimum Balance | Benefits |
|--------------|-----------------|----------|
| **Regular Customer** | N/A | Standard banking services |
| **Premium Customer** | $10,000 | No monthly fees, Priority service |

## 💻 System Requirements

- **Java**: JDK 17 or higher
- **Build Tool**: Apache Maven 3.8+
- **IDE**: IntelliJ IDEA (recommended) or any Java IDE
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 512MB RAM

## 📂 Project Structure

```
bam/
├── src/
│   └── com/
│       └── bam/
│           ├── Main.java                    # Application entry point
│           ├── exceptions/                  # Custom exception classes
│           │   ├── InsufficientFundsException.java
│           │   ├── InsufficientInitialDepositException.java
│           │   ├── InvalidAccountException.java
│           │   ├── InvalidAccountNumberException.java
│           │   ├── InvalidAgeException.java
│           │   ├── InvalidChoiceException.java
│           │   ├── InvalidContactException.java
│           │   ├── InvalidDepositAmountException.java
│           │   ├── InvalidEmailException.java
│           │   ├── InvalidNameException.java
│           │   ├── InvalidWithdrawalAmountException.java
│           │   └── OverdraftExceededException.java
│           ├── interfaces/                  # Interface definitions
│           │   └── Transactable.java
│           ├── models/                      # Domain models
│           │   ├── Account.java             # Abstract base account
│           │   ├── SavingsAccount.java
│           │   ├── CheckingAccount.java
│           │   ├── Customer.java            # Abstract base customer
│           │   ├── RegularCustomer.java
│           │   ├── PremiumCustomer.java
│           │   └── Transaction.java
│           ├── services/                    # Business logic services
│           │   ├── AccountManager.java
│           │   ├── TransactionManager.java
│           │   └── FilePersistenceService.java
│           └── utils/                       # Utility classes
│               ├── InputHandler.java
│               └── InputValidator.java
├── src/test/java/                          # Unit tests
│   └── models/
│       ├── AccountDepositTest.java
│       ├── AccountProcessTransactionTest.java
│       ├── AccountTestBase.java
│       ├── AccountTransferTest.java
│       ├── CheckingAccountWithdrawTest.java
│       ├── SavingsAccountWithdrawTest.java
│       ├── ConcurrentDepositsTest.java
│       ├── ConcurrentWithdrawalsTest.java
│       └── ConcurrentMixedTransactionsTest.java
├── data/                                   # Persistent data storage
│   ├── accounts.txt
│   └── transactions.txt
├── docs/                                   # Documentation
│   └── git-workflow.md
├── pom.xml                                 # Maven configuration
├── README.md                               # Project documentation
└── .gitignore                              # Git ignore rules
```

## 🚀 Quick Start

### Using Maven (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd bam

# Clean and compile
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="com.bam.Main"

# Run all tests
mvn test

# Package the application
mvn package
```

### Using Java CLI

```bash
# Compile all source files
javac -d target/classes -cp ".:lib/*" $(find src/com/bam -name "*.java")

# Run the application
java -cp target/classes com.bam.Main

# Run tests (requires JUnit on classpath)
java -cp "target/classes:target/test-classes:lib/*" org.junit.platform.console.ConsoleLauncher --scan-classpath
```

### Using IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select **File → Open** and choose the project directory
3. Wait for Maven to import dependencies
4. Right-click on `Main.java` and select **Run 'Main.main()'**

## 📖 Usage Guide

### Main Menu Navigation

```
=== Bank Account Management System ===
1. Manage Accounts
2. Perform Transactions
3. Generate Account Statements
4. Save/Load Data
5. Run tests
6. Run Concurrent Simulation
7. Exit
```

### Creating an Account

1. Select **Manage Accounts → Create Account**
2. Choose customer type (Regular/Premium)
3. Enter customer details:
   - Name (2-50 characters)
   - Age (18+)
   - Contact (10 digits)
   - Email (valid format)
   - Address
4. Choose account type (Savings/Checking)
5. Enter initial deposit (minimum requirements apply)
6. Confirm account creation

### Processing Transactions

1. Select **Perform Transactions**
2. Enter account number
3. Choose transaction type:
   - **Deposit**: Any positive amount
   - **Withdrawal**: Subject to balance/overdraft limits
   - **Transfer**: Transfer funds between accounts
4. Confirm transaction details
5. View updated balance

### Generating Statements

1. Select **Generate Account Statements**
2. Enter account number
3. View formatted statement with:
   - Account details
   - Transaction history (newest first)
   - Total deposits and withdrawals
   - Net change

### Running Tests

Select **Run tests** from the main menu to execute the JUnit test suite:
- Account deposit tests
- Withdrawal tests (Savings and Checking)
- Transfer tests
- Transaction processing tests
- Concurrent operation tests

### Concurrent Simulation

Test thread-safe operations:
1. Select **Run Concurrent Simulation**
2. Choose simulation type:
   - Concurrent Deposits
   - Concurrent Withdrawals
   - Mixed Transactions
   - Visual Simulation
3. Observe real-time transaction processing
4. Verify final balances are accurate

## 🏗️ Architecture

### Design Patterns Used

- **Inheritance**: Account and Customer hierarchies
- **Polymorphism**: Method overriding for account-specific behaviors
- **Encapsulation**: Private fields with public getters/setters
- **Composition**: Managers use collections of domain objects
- **Strategy Pattern**: Transaction processing via Transactable interface
- **Singleton-like**: Static counters for unique ID generation

### Key Classes

#### Account Hierarchy
```java
Account (abstract)
├── SavingsAccount
└── CheckingAccount
```

#### Customer Hierarchy
```java
Customer (abstract)
├── RegularCustomer
└── PremiumCustomer
```

### Data Management

- **Collections**: `ArrayList` and `HashMap` for efficient data storage
- **Streams API**: Functional operations for filtering and sorting
- **File I/O**: Java NIO for persistent storage
- **Thread Safety**: Synchronized methods for concurrent access

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AccountDepositTest

# Run with verbose output
mvn test -X
```

### Test Coverage

- ✅ Account deposit operations
- ✅ Savings account withdrawals with minimum balance
- ✅ Checking account withdrawals with overdraft
- ✅ Account transfers
- ✅ Transaction processing
- ✅ Concurrent deposits
- ✅ Concurrent withdrawals
- ✅ Mixed concurrent transactions

### Test Results Location

```
target/surefire-reports/
├── TEST-test.java.models.AccountDepositTest.xml
├── TEST-test.java.models.CheckingAccountWithdrawTest.xml
└── ...
```

## 💾 Data Persistence

### File Format

**accounts.txt**
```
ACC001|John Doe|30|555-0101|john@example.com|123 Main St|Regular|Savings|5000.00|Active
```

**transactions.txt**
```
TXN001|ACC001|Deposit|500.00|5500.00|2024-12-17T10:30:00
```

### Auto-Save Feature

- Data is automatically saved on application exit
- Manual save/load available via menu option 4
- Files are stored in the `data/` directory

## ⚡ Concurrency Support

### Thread-Safe Operations

All account operations are synchronized to prevent:
- Race conditions
- Data inconsistency
- Lost updates

### Implementation

```java
public synchronized boolean processTransaction(double amount, String type) {
    // Thread-safe transaction logic
}
```

### Concurrent Testing

Run concurrent simulations to verify thread safety with multiple simultaneous operations on the same account.

## 🔄 Development Workflow

See [docs/git-workflow.md](docs/git-workflow.md) for detailed Git workflow documentation including:
- Feature branch strategy
- Cherry-pick usage
- Commit conventions
- Merging practices

### Quick Reference

```bash
# Create feature branch
git checkout -b feature/new-feature

# Commit changes
git add .
git commit -m "feat: Add new feature description"

# Merge to main
git checkout main
git merge feature/new-feature
```

## ✅ Requirements Checklist

### Week 1 Requirements
- [x] All 11 required classes implemented
- [x] All 5 user stories fully functional
- [x] Unique ID generation (ACC001, TXN001, etc.)
- [x] Comprehensive input validation
- [x] Minimum balance enforcement for Savings accounts
- [x] Overdraft limit enforcement for Checking accounts
- [x] Console-based menu system
- [x] Account and transaction management

### Week 2 Enhancements
- [x] Custom exception classes (12 exceptions)
- [x] Try-catch error handling
- [x] Code refactoring and modularization
- [x] JavaDoc documentation
- [x] JUnit 5 test suite
- [x] Git version control with branching
- [x] Cherry-pick demonstrations

### Week 3 Advanced Features
- [x] Migration to Java Collections (ArrayList, HashMap)
- [x] Functional programming with Streams API
- [x] File persistence with Java NIO
- [x] Regex validation for inputs
- [x] Thread-safe concurrent operations
- [x] Synchronized methods
- [x] Parallel stream processing
- [x] Concurrent transaction tests

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Add JavaDoc comments for all public methods
- Write unit tests for new features
- Keep methods under 25 lines when possible

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Bernard Mawulorm Kofi Wodoame**

## 🙏 Acknowledgments

- Course instructors for project requirements
- JUnit team for testing framework
- Java community for best practices

## 📞 Support

For questions or issues:
1. Check existing [documentation](docs/)
2. Review [Git workflow guide](docs/git-workflow.md)
3. Open an issue on GitHub

---

**Last Updated:** December 17, 2025  
**Version:** 1.0.0  
**Java Version:** 17+
