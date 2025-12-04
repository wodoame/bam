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

Changes to make:
Create some seed data
Validate some of the data inputted by the user
Organize the code into packages for better structure
use the processTransaction method in handling the transactions
Show better error messages

Changes I've made:
1. Used `processTransaction` method in performing transactions. It replaced the direct use of `deposit` and `withdraw` methods
2. Created an `InputHandler` class to validate user inputs