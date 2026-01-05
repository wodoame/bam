package test.java.models;

import com.bam.exceptions.InsufficientFundsException;
import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.models.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountWithdrawTest extends AccountTestBase {

    @Test
    void withdrawWithinLimitsSucceeds() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);

        boolean result = account.processTransaction(300, "withdrawal");

        assertTrue(result);
        assertEquals(700, account.getBalance());
    }

    @Test
    void withdrawBelowMinimumBalanceFails() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 600);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(200));
    }

    @Test
    void withdrawRejectsNonPositiveAmount() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);

        assertThrows(InvalidWithdrawalAmountException.class, () -> account.withdraw(0));
        assertThrows(InvalidWithdrawalAmountException.class, () -> account.withdraw(-100));
    }

    @Test
    void withdrawExactMinimumBalanceLeavesMinimum() {
        // Initial deposit 1000, Minimum balance 500
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);
        // Withdraw 500, remaining should be 500 (Minimum Balance)
        boolean result = account.processTransaction(500, "withdrawal");
        assertTrue(result);
        assertEquals(500, account.getBalance());
        assertEquals(SavingsAccount.MINIMUM_BALANCE, account.getBalance());
    }
}
