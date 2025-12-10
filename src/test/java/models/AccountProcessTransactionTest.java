package test.java.models;

import com.bam.models.CheckingAccount;
import com.bam.models.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountProcessTransactionTest extends AccountTestBase {

    @Test
    void processTransactionTransferSuccess() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);

        boolean result = checking.processTransaction(200, "Transfer", savings);

        assertTrue(result);
        assertEquals(800, checking.getBalance());
        assertEquals(700, savings.getBalance());
    }

    @Test
    void processTransactionTransferFailsAndHandlesException() {
        // Savings min balance is 500. Balance 500. Transferring 100 out would leave
        // 400.
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);

        // We expect custom error message printed to std out, but method returns false
        boolean result = savings.processTransaction(100, "Transfer", checking);

        assertFalse(result);
        assertEquals(500, savings.getBalance());
        assertEquals(1000, checking.getBalance());
    }

    @Test
    void processTransactionInvalidTypeReturnsFalse() {
        CheckingAccount checking = new CheckingAccount(regularCustomer, 1000);
        SavingsAccount savings = new SavingsAccount(regularCustomer, 500);

        boolean result = checking.processTransaction(200, "InvalidType", savings);

        assertFalse(result);
        assertEquals(1000, checking.getBalance());
    }
}
