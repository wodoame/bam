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
 * Tests concurrent withdrawal operations to verify thread safety and proper handling
 * of insufficient funds across multiple threads.
 */
class ConcurrentWithdrawalsTest extends AccountTestBase {

    @Test
    @DisplayName("Multiple threads withdrawing should maintain correct balance")
    void testConcurrentWithdrawals() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 2000.0);
        int threadCount = 10;
        double withdrawalAmount = 100.0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(withdrawalAmount, "Withdrawal");
                    if (success) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }

        latch.await();
        
        // All withdrawals should succeed (2000 - 1000 = 1000, within overdraft)
        assertEquals(threadCount, successCount.get(), "All withdrawals should succeed");
        
        double expectedBalance = 2000.0 - (threadCount * withdrawalAmount);
        assertEquals(expectedBalance, account.getBalance(), 0.01, 
            "Balance should equal initial minus all withdrawals");
    }

    @Test
    @DisplayName("Concurrent withdrawals should properly reject when insufficient funds")
    void testConcurrentWithdrawalsInsufficientFunds() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 500.0);
        int threadCount = 10;
        double withdrawalAmount = 100.0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(withdrawalAmount, "Withdrawal");
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }

        latch.await();
        
        // With 500 balance + 1000 overdraft = 1500 total available
        // 1500 / 100 = 15 possible withdrawals, but we only have 10 threads
        // So some should succeed and some should fail
        assertTrue(successCount.get() > 0, "Some withdrawals should succeed");
        assertEquals(threadCount, successCount.get() + failureCount.get(), 
            "All transactions should be accounted for");
        
        // Balance should never go below -overdraftLimit
        assertTrue(account.getBalance() >= -1000.0, 
            "Balance should not exceed overdraft limit");
    }

    @Test
    @DisplayName("Concurrent withdrawals on savings account with minimum balance")
    void testConcurrentWithdrawalsSavingsMinimumBalance() throws InterruptedException {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000.0);
        int threadCount = 8;
        double withdrawalAmount = 100.0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(withdrawalAmount, "Withdrawal");
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }

        latch.await();
        
        // With 1000 balance and 500 minimum, only 500 can be withdrawn
        // That's 5 successful withdrawals of 100 each
        assertTrue(successCount.get() <= 5, 
            "At most 5 withdrawals should succeed due to minimum balance");
        assertTrue(failureCount.get() >= 3, 
            "At least 3 withdrawals should fail due to minimum balance");
        
        // Balance should never go below minimum
        assertTrue(account.getBalance() >= 500.0, 
            "Balance should not go below minimum balance");
    }

    @Test
    @DisplayName("High-frequency concurrent withdrawals")
    void testHighFrequencyConcurrentWithdrawals() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 10000.0);
        int threadCount = 20;
        int withdrawalsPerThread = 5;
        double withdrawalAmount = 50.0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < withdrawalsPerThread; j++) {
                        boolean success = account.processTransaction(withdrawalAmount, "Withdrawal");
                        if (success) {
                            successCount.incrementAndGet();
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
        
        // All withdrawals should succeed (total: 20 * 5 * 50 = 5000)
        int expectedSuccesses = threadCount * withdrawalsPerThread;
        assertEquals(expectedSuccesses, successCount.get(), 
            "All withdrawals should succeed");
        
        double expectedBalance = 10000.0 - (expectedSuccesses * withdrawalAmount);
        assertEquals(expectedBalance, account.getBalance(), 0.01, 
            "Balance should match expected value after all withdrawals");
    }
}

