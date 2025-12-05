`AccountManager`
  - `Attributes`
    - `private final Account[] accounts;`: Stores all accounts in the system
  - `Methods`
    - `public void viewAllAccounts() {`:Displays all accounts in the system

`Main`
  - `Methods`
    - `private static void printMenu() {`: Prints the main menu options
    - `private static void createAccount() {`: Handles account creation

`TransactionManager`: This class manages transactions within the banking system
  - Attributes
    - `private final Transaction[] transactions;`: Stores all transactions in the system
    - `Methods`
    - `public void viewTransactionsByAccount(String accountNumber) {`: Displays transactions for a specific account
  
`Account`
  - Attributes
    - `private String accountNumber;`: Unique identifier for the account
      - `private String accountHolderName;`: Name of the account holder
  - Methods
  - ``
`InputHandler` class accepts inputs in their correct format with an error message if the format does not match
`InputValidator` class validates the inputs provided by the user

Changes to make:
Create some seed data
Validate some of the data inputted by the user
Organize the code into packages for better structure
use the processTransaction method in handling the transactions
Show better error messages

Git:
Create branches for different features

Testing:
Write unit tests for critical methods

Extras:
Add functionality to view transaction history for each account and each account details

Exceptions:
- Example methods affected are: `getName`, `getContact`, `getAccount`, etc.
- validate withdrawals (e.g., insufficient funds)
- `InvalidAccountException`: Custom exception for invalid account operations
- `InsufficientFundsException`: Custom exception for insufficient funds during withdrawal
- `InvalidAgeException`: Custom exception for invalid age input
- `InsufficientInitialDepositException`: Custom exception for invalid age input
- `InvalidContactException`: Custom exception for invalid age input
- `InvalidNameException`: Custom exception for invalid age input
- Add exceptions for invalid choices in the menu


Changes I've made:
1. Used `processTransaction` method in performing transactions. It replaced the direct use of `deposit` and `withdraw` methods
2. Created an `InputHandler` class to validate user inputs
3. Added input validation to the `InputHandler` class. Some inputs such as the age and name of the customer are now validated
4. Create custom exceptions such as `InvalidAccountException` to handle wrong inputs
5. Add Seed data for accounts

Random:
Show confirmation messages only when amount is valid
1. deposit(amount)
2. validateDepositAmount