package test.java.models;

import com.bam.exceptions.InsufficientFundsException;
import com.bam.exceptions.InvalidAccountException;
import com.bam.models.CheckingAccount;
import com.bam.models.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTransferTest extends AccountTestBase {

    @Test
    void transferCheckingToSavingsSuccess() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);

        checking.transfer(savings, 200);

        assertEquals(800, checking.getBalance());
        assertEquals(700, savings.getBalance());
    }

    @Test
    void transferInsufficientFundsFails() {
        // Savings min balance is 500. Balance 500.
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);

        // Try to transfer 100. This would result in savings balance 400 < 500.
        assertThrows(InsufficientFundsException.class, () -> savings.transfer(checking, 100));

        // Balances should remain unchanged
        assertEquals(500, savings.getBalance());
        assertEquals(1000, checking.getBalance());
    }

    @Test
    void transferToSelfFails() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        assertThrows(InvalidAccountException.class, () -> checking.transfer(checking, 100));
    }

    @Test
    void transferSavingsToCheckingSuccess() {
        SavingsAccount savings = new SavingsAccount(regularCustomer, 1000);
        CheckingAccount checking = new CheckingAccount(regularCustomer, 500);

        // Savings min is 500. Transfer 200 => 800 (OK)
        savings.transfer(checking, 200);

        assertEquals(800, savings.getBalance());
        assertEquals(700, checking.getBalance());
    }

    @Test
    void transferCheckingOverdraftSuccess() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 100);
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);

        // Transfer 200. Balance became -100. Overdraft limit 1000. OK.
        checking.transfer(savings, 200);

        assertEquals(-100, checking.getBalance());
        assertEquals(700, savings.getBalance());
    }

    @Test
    void transferNegativeAmountFails() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);

        assertThrows(com.bam.exceptions.InvalidWithdrawalAmountException.class, () -> checking.transfer(savings, -100));

        assertEquals(1000, checking.getBalance());
        assertEquals(500, savings.getBalance());
    }

    @Test
    void processTransactionWrapperSuccess() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);

        boolean result = checking.processTransaction(200, "Transfer", savings);

        assertTrue(result);
        assertEquals(800, checking.getBalance());
        assertEquals(700, savings.getBalance());
    }

    @Test
    void processTransactionWrapperFail() {
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);

        // Fail due to min balance
        boolean result = savings.processTransaction(100, "Transfer", checking);

        assertFalse(result);
        assertEquals(500, savings.getBalance());
        assertEquals(1000, checking.getBalance());
    }

    @Test
    void transferToNullFails() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        assertThrows(InvalidAccountException.class, () -> checking.transfer(null, 100));
    }
}
