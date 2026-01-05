package test.java.models;

import com.bam.exceptions.InvalidWithdrawalAmountException;
import com.bam.exceptions.OverdraftExceededException;
import com.bam.models.CheckingAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountWithdrawTest extends AccountTestBase {

    @Test
    void withdrawWithinBalanceSucceeds() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 500);

        boolean result = account.processTransaction(200, "withdrawal");

        assertTrue(result);
        assertEquals(300, account.getBalance());
    }

    @Test
    void withdrawWithinOverdraftLimitSucceeds() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 100);

        boolean result = account.processTransaction(900, "withdrawal");

        assertTrue(result);
        assertEquals(-800, account.getBalance());
    }

    @Test
    void withdrawBeyondOverdraftLimitThrowsException() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 0);

        assertThrows(OverdraftExceededException.class, () -> account.withdraw(1100));
        assertEquals(0, account.getBalance());
    }

    @Test
    void withdrawRejectsNonPositiveAmount() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 500);
        assertThrows(InvalidWithdrawalAmountException.class, () -> account.withdraw(0));
        assertEquals(500, account.getBalance());
        assertThrows(InvalidWithdrawalAmountException.class, () -> account.withdraw(-50));
        assertEquals(500, account.getBalance());
    }

    @Test
    void withdrawExactBalanceLeavesZero() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 500);
        boolean result = account.processTransaction(500, "withdrawal");
        assertTrue(result);
        assertEquals(0, account.getBalance());
    }

    @Test
    void withdrawExactOverdraftLimitLeavesNegativeLimit() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 0);
        // Overdraft limit is 1000. Balance 0 - 1000 = -1000
        boolean result = account.processTransaction(CheckingAccount.OVERDRAFT_LIMIT, "withdrawal");
        assertTrue(result);
        assertEquals(-CheckingAccount.OVERDRAFT_LIMIT, account.getBalance());
    }
}
