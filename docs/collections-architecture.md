# Collections Architecture

## Overview

This document describes how Java Collections Framework is utilized throughout the Bank Account Management (BAM) system. The project demonstrates strategic use of various collection types to manage accounts, transactions, and concurrent operations efficiently.

---

## Core Collection Usage

### 1. ArrayList - Account Storage

**Location**: `AccountManager.java`

**Declaration**:
```java
private final ArrayList<Account> accounts;
```

**Purpose**: 
- Primary storage for all bank accounts in the system
- Maintains insertion order for sequential access
- Provides fast indexed access for iterations and display operations

**Key Operations**:
- **Add**: `accounts.add(account)` - Registers new accounts
- **Iteration**: Used with streams for sorting and filtering in `viewAllAccounts()`
- **Size**: `accounts.size()` - Returns total account count
- **Clear**: `accounts.clear()` - Used during data reload operations
- **Copy**: `new ArrayList<>(accounts)` - Creates defensive copies via `getAccountsSnapshot()`

**Rationale**: ArrayList was chosen for:
- Dynamic resizing as accounts are added
- Fast sequential access for displaying account listings
- Stream API compatibility for sorting and aggregation operations
- Simple API for common operations like adding and iterating

---

### 2. HashMap - Account Lookup

**Location**: `AccountManager.java`

**Declaration**:
```java
private final Map<String, Account> accountLookup;
```

**Purpose**: 
- Provides O(1) average-case lookup for accounts by account number
- Ensures account number uniqueness through key constraints
- Optimizes frequent search operations

**Key Operations**:
- **Insert**: `accountLookup.putIfAbsent(accountNumber, account)` - Atomically adds new accounts while preventing duplicates
- **Lookup**: `accountLookup.get(accountNumber)` - Fast retrieval by account number
- **Clear**: `accountLookup.clear()` - Used during data reload

**Rationale**: HashMap was chosen for:
- Constant-time account retrieval by number
- Built-in duplicate prevention via key uniqueness
- Memory-efficient storage of key-value pairs
- Thread-safe atomic operations with `putIfAbsent()`

---

### 3. HashMap - Transaction Ledger (Multi-valued)

**Location**: `TransactionManager.java`

**Declaration**:
```java
private static final Map<String, List<Transaction>> transactionsMap = new HashMap<>();
```

**Purpose**: 
- Maps account numbers to their transaction histories
- Maintains separate transaction lists per account
- Enables efficient transaction retrieval by account

**Key Operations**:
- **Compute**: `transactionsMap.computeIfAbsent(accountNumber, key -> new ArrayList<>())` - Lazily creates transaction lists
- **Get**: `transactionsMap.getOrDefault(accountNumber, List.of())` - Safely retrieves transaction history
- **Clear**: `transactionsMap.clear()` - Used during transaction reload
- **Stream**: Flattens all transaction lists for global operations

**Rationale**: HashMap with List values provides:
- O(1) access to an account's transaction history
- Automatic grouping of transactions by account
- Efficient addition of new transactions to specific accounts
- Easy iteration over all transactions via flat-mapping

---

### 4. HashMap - Transaction Counters

**Location**: `TransactionManager.java`

**Declaration**:
```java
private static final Map<String, Integer> transactionCounters = new HashMap<>();
```

**Purpose**: 
- Tracks transaction count per account for ID generation
- Ensures sequential transaction IDs within each account
- Maintains state across transaction additions

**Key Operations**:
- **Update**: Increments counter when transactions are added
- **Retrieve**: Gets current count for generating next transaction ID
- **Clear**: Resets during data reload

**Rationale**: Separate counter map provides:
- Fast counter updates without scanning transaction lists
- Independent counter management for each account
- Simple integer-based ID generation

---

### 5. ArrayList - Transaction Lists (Nested)

**Location**: Within `TransactionManager.transactionsMap` values

**Declaration**:
```java
List<Transaction> accountTransactions = new ArrayList<>();
```

**Purpose**: 
- Stores chronological transaction history for each account
- Maintains insertion order (time-based)
- Supports sorting and filtering operations

**Key Operations**:
- **Add**: Appends new transactions in chronological order
- **Stream**: Filters, sorts, and transforms transactions
- **Max**: Finds most recent transaction using `Comparator.comparing()`
- **Filter**: Applies predicates for transaction queries

**Rationale**: ArrayList for transaction lists provides:
- Preservation of chronological order
- Efficient append operations for new transactions
- Stream API support for complex queries
- Sorting capabilities for display operations

---

## Stream API Integration

### Filtering and Mapping

**Location**: `TransactionManager.java`

**Example**:
```java
public List<Transaction> getTransactionsMatching(String accountNumber, Predicate<Transaction> predicate) {
    return getTransactions(accountNumber).stream()
            .filter(predicate)
            .collect(Collectors.toList());
}
```

**Purpose**: Enables declarative transaction queries with custom predicates

---

### Aggregation Operations

**Location**: `TransactionManager.java`

**Example**:
```java
public double calculateTotalTransaction(String accountNumber, String type) {
    return getTransactions(accountNumber).stream()
            .filter(txn -> txn.getType().equalsIgnoreCase(type))
            .mapToDouble(Transaction::getAmount)
            .sum();
}
```

**Purpose**: Calculates transaction totals by type (deposits, withdrawals, transfers)

---

### Flat-Mapping

**Location**: `TransactionManager.java`

**Example**:
```java
public List<Transaction> snapshotAllTransactions() {
    return transactionsMap.values().stream()
            .flatMap(List::stream)
            .map(TransactionManager::cloneTransaction)
            .collect(Collectors.toList());
}
```

**Purpose**: Combines all account transaction lists into a single unified list

---

### Sorting

**Location**: Multiple locations

**Examples**:

1. **Account sorting by number**:
```java
accounts.stream()
    .sorted(Comparator.comparing(Account::getAccountNumber))
    .forEach(account -> { ... });
```

2. **Transaction sorting by timestamp**:
```java
return getTransactions(accountNumber).stream()
    .max(Comparator.comparing(Transaction::getTimestamp));
```

**Purpose**: Orders data for consistent display and retrieval of most recent items

---

## Concurrent Collections Usage

### 1. ArrayList - Thread Coordination

**Location**: Test files (`ConcurrentDepositsTest.java`, etc.)

**Declaration**:
```java
List<Thread> threads = new ArrayList<>();
```

**Purpose**: 
- Manages multiple threads for concurrent testing
- Coordinates thread lifecycle (start, join)
- Ensures all threads complete before assertions

**Key Operations**:
- **Add**: `threads.add(thread)` - Registers test threads
- **Start**: Launches all threads via iteration
- **Join**: Waits for completion via `latch.await()`

---

### 2. CountDownLatch - Synchronization

**Location**: Test files

**Declaration**:
```java
CountDownLatch latch = new CountDownLatch(threadCount);
```

**Purpose**: 
- Coordinates concurrent thread execution
- Ensures all threads complete before verification
- Provides barrier synchronization

**Usage Pattern**:
```java
Thread thread = new Thread(() -> {
    try {
        // Perform operations
    } finally {
        latch.countDown();
    }
});
latch.await(); // Wait for all threads
```

---

### 3. AtomicInteger - Thread-Safe Counters

**Location**: Test files

**Declaration**:
```java
AtomicInteger successCount = new AtomicInteger(0);
```

**Purpose**: 
- Counts successful operations across threads
- Provides lock-free atomic increments
- Enables verification of concurrent operation success rates

---

## Collection Choice Rationale

### Why ArrayList for Accounts?

1. **Sequential Access**: Account listings require iterating all accounts
2. **Dynamic Size**: Number of accounts grows over time
3. **Order Preservation**: Maintains natural insertion order
4. **Stream Compatibility**: Enables sorting, filtering, and aggregation
5. **Simple API**: Clear, straightforward operations for basic use cases

### Why HashMap for Account Lookup?

1. **Fast Retrieval**: O(1) average-case lookup by account number
2. **Uniqueness**: Built-in duplicate prevention via keys
3. **Validation**: `putIfAbsent()` provides atomic duplicate checking
4. **Memory Efficiency**: Stores only necessary key-value pairs

### Why HashMap<String, List<Transaction>>?

1. **Logical Grouping**: Naturally groups transactions by account
2. **Efficient Access**: O(1) access to an account's transaction list
3. **Flexible Structure**: Each account can have any number of transactions
4. **Easy Aggregation**: Simple to process all transactions via flat-mapping

### Why ArrayList for Transaction Lists?

1. **Chronological Order**: Preserves transaction insertion order (time-based)
2. **Append Efficiency**: New transactions added at end in O(1) amortized time
3. **Sorting Support**: Can be sorted by various criteria for display
4. **Stream Operations**: Enables filtering, mapping, and aggregation

---

## Thread Safety Considerations

### Synchronized Blocks

**Location**: `TransactionManager.java`

```java
private static final Object ledgerLock = new Object();

synchronized (ledgerLock) {
    transactionsMap.computeIfAbsent(accountNumber, key -> new ArrayList<>()).add(transaction);
}
```

**Purpose**: 
- Protects concurrent access to shared collections
- Ensures atomic read-modify-write operations
- Prevents race conditions during transaction addition

### Immutable Returns

**Location**: `TransactionManager.java`

```java
public static List<Transaction> getTransactions(String accountNumber) {
    return transactionsMap.getOrDefault(accountNumber, List.of());
}
```

**Purpose**: 
- Returns unmodifiable empty lists via `List.of()`
- Prevents external modification of internal state
- Ensures data integrity

### Defensive Copying

**Location**: `AccountManager.java`

```java
public ArrayList<Account> getAccountsSnapshot() {
    return new ArrayList<>(accounts);
}
```

**Purpose**: 
- Creates independent copy of internal collection
- Prevents external modification of manager's state
- Enables safe iteration outside synchronization

---

## Performance Characteristics

### Time Complexity Summary

| Operation | Collection | Complexity | Use Case |
|-----------|-----------|------------|----------|
| Add account | ArrayList | O(1) amortized | Account creation |
| Lookup account | HashMap | O(1) average | Find by account number |
| List all accounts | ArrayList | O(n) | Display account listing |
| Add transaction | HashMap + ArrayList | O(1) average | Record transaction |
| Get account transactions | HashMap | O(1) average | Retrieve transaction history |
| Sort transactions | ArrayList | O(n log n) | Display sorted history |
| Stream operations | Various | O(n) | Filtering, mapping, aggregation |

### Space Complexity

- **Accounts**: O(n) where n = number of accounts
- **Account Lookup**: O(n) where n = number of accounts (duplicate storage for fast access)
- **Transactions**: O(m) where m = total number of transactions across all accounts
- **Transaction Counters**: O(n) where n = number of accounts with transactions

---

## Best Practices Demonstrated

### 1. Dual Data Structures

The project maintains both `ArrayList<Account>` and `Map<String, Account>` to optimize for different access patterns:
- ArrayList for iteration and display
- HashMap for fast lookups

**Trade-off**: Uses more memory but provides optimal performance for both use cases.

### 2. Lazy Initialization

```java
transactionsMap.computeIfAbsent(accountNumber, key -> new ArrayList<>())
```

Creates transaction lists only when needed, saving memory for accounts without transactions.

### 3. Defensive Copying

```java
public ArrayList<Account> getAccountsSnapshot() {
    return new ArrayList<>(accounts);
}
```

Prevents external code from modifying internal collections.

### 4. Immutable Empty Collections

```java
return transactionsMap.getOrDefault(accountNumber, List.of());
```

Returns immutable empty lists instead of null, preventing NullPointerExceptions.

### 5. Stream API Usage

Leverages functional programming for cleaner, more maintainable code:
- Filtering: `filter(predicate)`
- Transformation: `map(function)`
- Aggregation: `sum()`, `collect()`
- Sorting: `sorted(comparator)`

### 6. Synchronization for Thread Safety

Uses `synchronized` blocks with dedicated lock objects to protect shared collections in multi-threaded scenarios.

---

## Future Enhancements

### Potential Optimizations

1. **ConcurrentHashMap**: Replace `HashMap` with `ConcurrentHashMap` for better concurrent performance
2. **LinkedHashMap**: Use if LRU caching of recent accounts becomes necessary
3. **TreeMap**: Consider for automatic sorting of accounts by number
4. **CopyOnWriteArrayList**: May be suitable for transaction lists with more reads than writes
5. **BlockingQueue**: Could be used for asynchronous transaction processing

### Scalability Considerations

For larger deployments, consider:
- Database-backed storage instead of in-memory collections
- Pagination for large account lists
- Indexing strategies for complex queries
- Distributed caching solutions (Redis, Memcached)

---

## Summary

The BAM system demonstrates effective use of the Java Collections Framework through:

1. **Strategic collection choices** based on access patterns
2. **Stream API integration** for declarative data processing
3. **Thread safety mechanisms** for concurrent operations
4. **Defensive programming** with immutable returns and copying
5. **Performance optimization** via dual data structures
6. **Clean, maintainable code** leveraging modern Java features

The architecture balances memory usage, performance, and code clarity while providing a robust foundation for banking operations.

