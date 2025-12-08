package test.java.models;

import com.bam.exceptions.InvalidDepositAmountException;
import com.bam.models.CheckingAccount;
import com.bam.models.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountDepositTest extends AccountTestBase {

    @Test
    void depositAddsFundsForSavingsAccount() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);

        boolean result = account.deposit(200);

        assertTrue(result);
        assertEquals(1200, account.getBalance());
    }

    @Test
    void depositAddsFundsForCheckingAccount() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 500);

        boolean result = account.deposit(300);

        assertTrue(result);
        assertEquals(800, account.getBalance());
    }

    @Test
    void depositThrowsExceptionWithZeroOrNegativeAmount() {
        SavingsAccount savingsAccount = new SavingsAccount(regularCustomer, 1000);
        CheckingAccount checkingAccount = new CheckingAccount(regularCustomer, 1000);

        assertThrows(InvalidDepositAmountException.class, () -> savingsAccount.deposit(0));
        assertThrows(InvalidDepositAmountException.class, () -> savingsAccount.deposit(-50));
        assertThrows(InvalidDepositAmountException.class, () -> checkingAccount.deposit(0));
        assertThrows(InvalidDepositAmountException.class, () -> checkingAccount.deposit(-50));
    }
}

