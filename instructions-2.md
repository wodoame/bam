This is a detailed outline of the **Bank Account Management** project, focusing on refactoring, exception handling, testing, and Git version control, based on the provided document.

-----

# 🏦 Bank Account Management System (Week 2 Enhancement)

  * **Complexity:** Medium
  * **Time Estimate:** 6-8 hours
  * **Technology Stack:** Java 21 (LTS), IntelliJ IDEA, JUnit 5, Git

This project extends the Week 1 console-based Bank Account Management System, emphasizing **Clean Code**, **Exception Handling**, **Unit Testing**, and **Version Control** practices.

-----

## ✨ Learning Objectives

By completing this lab, you will be able to:

  * Apply **clean code principles** (readability, maintainability, scalability).
  * Implement robust **exception handling** (try-catch, `throws`, custom exceptions).
  * Write and execute **unit tests with JUnit 5** for core methods.
  * Utilize **Git for version control** (init, commit, branching, merging, `cherry-pick`).
  * **Refactor** existing classes to improve structure and reduce redundancy.
  * Prepare the codebase for future enhancements (Java Collections, file persistence).

-----

## 🛠️ System Features Overview

The enhanced system includes five core features:

1.  **Refactored Account and Transaction Classes:** Simplify methods, apply modular design, introduce helper methods (e.g., `validateAmount()`).
2.  **Error Handling and Validation:** Handle invalid inputs with `try-catch`, throw **custom exceptions** (e.g., `InsufficientFundsException`, `InvalidAccountException`), and enforce transaction limits.
3.  **Transaction Testing and Verification:** Write **JUnit tests** for `deposit()`, `withdraw()`, and `transfer()`.
4.  **Git Version Control Integration:** Initialize repository, use **feature branches**, and apply `git cherry-pick`.
5.  **Enhanced Console User Experience:** Clear error messages, confirmation prompts, and simulated test output.

-----

## 📂 Project Structure

```
bank-account-management-system/
├── src/
│   ├── Main.java
│   ├── models/
│   │   ├── Account.java
│   │   ├── ... (SavingsAccount, Customer, Transaction, etc.)
│   ├── exceptions/
│   │   ├── InvalidAmountException.java
│   │   ├── InsufficientFundsException.java
│   │   └── OverdraftExceededException.java
│   ├── services/
│   │   ├── AccountManager.java
│   │   ├── TransactionManager.java
│   │   └── StatementGenerator.java
│   ├── utils/
│   │   └── ValidationUtils.java
│   └── src/test/java/
│       ├── AccountTest.java
│       ├── TransactionManagerTest.java
│       └── ExceptionTest.java
└── docs/
    ├── README.md
    └── git-workflow.md
```

-----

## 📜 User Stories (Key Requirements)

| Epic | User Story (US) | Description & Criteria | Technical Requirements |
| :--- | :--- | :--- | :--- |
| **Epic 1: Error Handling** | **US-1.1:** Handle Invalid Deposits | Negative deposit amounts throw `InvalidAmountException`. | Use `try-catch`; define custom exceptions. |
| | **US-1.2:** Prevent Overdraft Exceeding Limit | Withdrawals beyond the limit trigger `OverdraftExceededException`. | |
| **Epic 2: Clean Design** | **US-2.1:** Refactor TransactionManager | Break long methods into modular ones ($\le 25$ lines); rename variables. | Follow Google Java Style Guide; add JavaDoc. |
| | **US-2.2:** Apply Comments and Formatting | Follow Java Style Guide; add JavaDoc to all public methods. | |
| **Epic 3: Testing** | **US-3.1:** Write Unit Tests for Deposit and Withdraw | Tests must pass for valid and invalid cases. | Use **JUnit 5**; organize tests under `src/test/java`; use `@BeforeEach`. |
| | **US-3.2:** Test Transfer Between Accounts | Check balance updates in both accounts. | |
| **Epic 4: Git Workflow** | **US-4.1:** Implement Feature Branching | Create and switch branches (`git branch`, `git checkout`). | Include Git commands in `README`; perform $\ge 3$ commits. |
| | **US-4.2:** Cherry-Pick Specific Commits | Selectively apply tested commits across branches. | |
| **Epic 5: Statement Gen** | **US-5.1:** Generate Error-Free Statements | Handle accounts with no transactions gracefully; format output. | Sort transactions by timestamp (newest first); use 2-decimal precision. |

-----

## 💻 Implementation Phases and Git Workflow

| Phase | Tasks | Estimated Time | Key Git Commands |
| :--- | :--- | :--- | :--- |
| **1: Setup & Refactoring** | Fork/Setup repo; create `feature/refactor` branch; refactor core classes; add JavaDocs. | 1-2 hours | `git checkout -b feature/refactor` |
| **2: Exception Handling** | Create custom exceptions; wrap input validation in `try-catch`; update UI error display. | 1-2 hours | `git checkout -b feature/exceptions` |
| **3: Testing & Verification** | Add JUnit 5; write unit tests for core transaction methods; run tests. | 2 hours | `git checkout -b feature/testing`; `git cherry-pick <refactor-commit-hash>` |
| **4: Merge & Documentation** | Merge branches (`git merge`); resolve conflicts; document Git workflow; submit. | 1-2 hours | `git checkout main`; `git merge feature/exceptions`; `git push` |

-----

## ✅ Minimum Requirements Checklist

  * All custom exceptions implemented.
  * JUnit tests created and passing.
  * Code refactored for clean structure and modularity.
  * Git repository initialized with **branching** and **cherry-pick** usage.
  * `README` includes Git workflow and test results.
  * All Week 1 features remain functional and enhanced.

-----

## 🧪 Example Test Scenarios

| Scenario | Focus | Expected Outcome |
| :--- | :--- | :--- |
| **Deposit Failure** | Exception Handling | Entering a negative amount displays `InvalidAmountException` message. |
| **Overdraft Limit** | Validation Logic | Withdrawal succeeds if within the overdraft limit; fails with `OverdraftExceededException` if not. |
| **Run JUnit Tests** | Testing | Option 4 runs tests; all assertions pass, and results display a summary (e.g., "All 4 tests passed successfully\!"). |
| **Statement Generation** | Refactoring/Clean Code | Output is clean, formatted, with transactions in reverse chronological order and accurate 2-decimal totals. |
| **Git Workflow** | Version Control | Successfully use `git cherry-pick` to apply selected refactoring/testing commits to a new branch. |

Would you like me to elaborate on a specific feature, like the custom exceptions or the JUnit testing structure?