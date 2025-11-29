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
  - 