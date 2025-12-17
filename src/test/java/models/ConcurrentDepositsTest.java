package test.java.models;

import com.bam.models.CheckingAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests concurrent deposit operations to verify thread safety.
 */
class ConcurrentDepositsTest extends AccountTestBase {

    @Test
    @DisplayName("Multiple threads depositing should maintain correct balance")
    void testConcurrentDeposits() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 1000.0);
        int threadCount = 10;
        double depositAmount = 100.0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    boolean success = account.processTransaction(depositAmount, "Deposit");
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

        // All deposits should succeed
        assertEquals(threadCount, successCount.get(), "All deposits should succeed");

        // Final balance should be initial + (threadCount * depositAmount)
        double expectedBalance = 1000.0 + (threadCount * depositAmount);
        assertEquals(expectedBalance, account.getBalance(), 0.01,
            "Balance should equal initial deposit plus all concurrent deposits");
    }

    @Test
    @DisplayName("High-frequency concurrent deposits should remain consistent")
    void testHighFrequencyConcurrentDeposits() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 5000.0);
        int threadCount = 20;
        int depositsPerThread = 5;
        double depositAmount = 50.0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < depositsPerThread; j++) {
                        boolean success = account.processTransaction(depositAmount, "Deposit");
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

        // All deposits should succeed
        int expectedSuccesses = threadCount * depositsPerThread;
        assertEquals(expectedSuccesses, successCount.get(), "All deposits should succeed");

        // Final balance verification
        double expectedBalance = 5000.0 + (expectedSuccesses * depositAmount);
        assertEquals(expectedBalance, account.getBalance(), 0.01,
            "Balance should match expected value after all concurrent deposits");
    }

    @Test
    @DisplayName("Concurrent deposits with varying amounts")
    void testConcurrentDepositsVaryingAmounts() throws InterruptedException {
        CheckingAccount account = new CheckingAccount(regularCustomer, 2000.0);
        int threadCount = 8;
        CountDownLatch latch = new CountDownLatch(threadCount);

        double[] amounts = {10.0, 25.0, 50.0, 75.0, 100.0, 150.0, 200.0, 250.0};
        double totalExpectedDeposits = 0.0;
        for (double amount : amounts) {
            totalExpectedDeposits += amount;
        }

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final double amount = amounts[i];
            Thread thread = new Thread(() -> {
                try {
                    account.processTransaction(amount, "Deposit");
                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }

        latch.await();

        double expectedBalance = 2000.0 + totalExpectedDeposits;
        assertEquals(expectedBalance, account.getBalance(), 0.01,
            "Balance should reflect all varying deposit amounts");
    }
}

