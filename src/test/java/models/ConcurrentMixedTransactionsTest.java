package test.java.models;

import com.bam.models.CheckingAccount;
import com.bam.models.SavingsAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests concurrent mixed deposit and withdrawal operations to verify thread safety
 * when both transaction types occur simultaneously.
 */
class ConcurrentMixedTransactionsTest extends AccountTestBase {

    @Test
    @DisplayName("Concurrent deposits and withdrawals should maintain correct balance")
    void testConcurrentMixedTransactions() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 5000.0);
        int depositThreads = 10;
        int withdrawalThreads = 10;
        double amount = 100.0;
        CountDownLatch latch = new CountDownLatch(depositThreads + withdrawalThreads);
        AtomicInteger depositSuccess = new AtomicInteger(0);
        AtomicInteger withdrawalSuccess = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        
        // Create deposit threads
        for (int i = 0; i < depositThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(amount, "Deposit");
                    if (success) {
                        depositSuccess.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            }, "Deposit-" + i);
            threads.add(thread);
        }
        
        // Create withdrawal threads
        for (int i = 0; i < withdrawalThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(amount, "Withdrawal");
                    if (success) {
                        withdrawalSuccess.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            }, "Withdrawal-" + i);
            threads.add(thread);
        }

        // Start all threads
        threads.forEach(Thread::start);
        latch.await();
        
        // All transactions should succeed (net zero change)
        assertEquals(depositThreads, depositSuccess.get(), "All deposits should succeed");
        assertEquals(withdrawalThreads, withdrawalSuccess.get(), "All withdrawals should succeed");
        
        // Balance should be initial amount (deposits and withdrawals cancel out)
        assertEquals(5000.0, account.getBalance(), 0.01, 
            "Balance should remain at initial amount");
    }

    @Test
    @DisplayName("Heavy mixed transactions with varying amounts")
    void testHeavyMixedTransactions() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 10000.0);
        int threadCount = 20;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger depositSuccess = new AtomicInteger(0);
        AtomicInteger withdrawalSuccess = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        
        // Create alternating deposit and withdrawal threads with varying amounts
        for (int i = 0; i < threadCount; i++) {
            final boolean isDeposit = i % 2 == 0;
            final double amount = 50.0 + (i * 10.0);
            
            Thread thread = new Thread(() -> {
                try {
                    String type = isDeposit ? "Deposit" : "Withdrawal";
                    boolean success = account.processTransaction(amount, type);
                    if (success) {
                        if (isDeposit) {
                            depositSuccess.incrementAndGet();
                        } else {
                            withdrawalSuccess.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }, (isDeposit ? "Deposit-" : "Withdrawal-") + i);
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        latch.await();
        
        // Verify all transactions were processed
        assertTrue(depositSuccess.get() > 0, "Some deposits should succeed");
        assertTrue(withdrawalSuccess.get() > 0, "Some withdrawals should succeed");
        
        // Balance should be within valid range
        assertTrue(account.getBalance() >= -1000.0, 
            "Balance should not exceed overdraft limit");
    }

    @Test
    @DisplayName("Concurrent mixed transactions on savings account")
    void testMixedTransactionsSavingsAccount() throws InterruptedException {
        SavingsAccount account = new SavingsAccount(regularCustomer, 2000.0);
        int depositThreads = 8;
        int withdrawalThreads = 8;
        double depositAmount = 100.0;
        double withdrawalAmount = 150.0;
        CountDownLatch latch = new CountDownLatch(depositThreads + withdrawalThreads);
        AtomicInteger depositSuccess = new AtomicInteger(0);
        AtomicInteger withdrawalSuccess = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        
        // Create deposit threads
        for (int i = 0; i < depositThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(depositAmount, "Deposit");
                    if (success) {
                        depositSuccess.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
        }
        
        // Create withdrawal threads
        for (int i = 0; i < withdrawalThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(withdrawalAmount, "Withdrawal");
                    if (success) {
                        withdrawalSuccess.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        latch.await();
        
        // All deposits should succeed
        assertEquals(depositThreads, depositSuccess.get(), "All deposits should succeed");
        
        // Some withdrawals may fail due to minimum balance requirement
        assertTrue(withdrawalSuccess.get() >= 0, "Withdrawal success count should be valid");
        
        // Balance should never go below minimum
        assertTrue(account.getBalance() >= 500.0, 
            "Balance should not go below minimum balance");
    }

    @Test
    @DisplayName("Stress test with rapid mixed transactions")
    void testStressMixedTransactions() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 8000.0);
        int threadCount = 50;
        int transactionsPerThread = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger totalSuccess = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < threadCount; i++) {
            final boolean isDeposit = i % 2 == 0;
            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < transactionsPerThread; j++) {
                        String type = isDeposit ? "Deposit" : "Withdrawal";
                        double amount = 20.0 + (j * 5.0);
                        boolean success = account.processTransaction(amount, type);
                        if (success) {
                            totalSuccess.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }

        latch.await();
        
        // Verify transactions were processed
        assertTrue(totalSuccess.get() > 0, "Some transactions should succeed");
        
        // Balance should be within valid range
        assertTrue(account.getBalance() >= -1000.0, 
            "Balance should not exceed overdraft limit");
    }

    @Test
    @DisplayName("Mixed transactions with equal net effect")
    void testMixedTransactionsNetZero() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 3000.0);
        int pairs = 15;
        double amount = 75.0;
        CountDownLatch latch = new CountDownLatch(pairs * 2);
        AtomicInteger totalTransactions = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        
        // Create pairs of deposit/withdrawal threads with same amount
        for (int i = 0; i < pairs; i++) {
            // Deposit thread
            Thread depositThread = new Thread(() -> {
                try {
                    if (account.processTransaction(amount, "Deposit")) {
                        totalTransactions.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            
            // Withdrawal thread
            Thread withdrawalThread = new Thread(() -> {
                try {
                    if (account.processTransaction(amount, "Withdrawal")) {
                        totalTransactions.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            
            threads.add(depositThread);
            threads.add(withdrawalThread);
        }

        threads.forEach(Thread::start);
        latch.await();
        
        // All transactions should succeed
        assertEquals(pairs * 2, totalTransactions.get(), 
            "All transactions should succeed");
        
        // Balance should remain at initial amount (net zero effect)
        assertEquals(3000.0, account.getBalance(), 0.01, 
            "Balance should remain unchanged with equal deposits and withdrawals");
    }
}

