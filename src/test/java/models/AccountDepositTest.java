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

        boolean result = account.processTransaction(200, "deposit");

        assertTrue(result);
        assertEquals(1200, account.getBalance());
    }

    @Test
    void depositAddsFundsForCheckingAccount() {
        CheckingAccount account = new CheckingAccount(regularCustomer, 500);

        boolean result = account.processTransaction(300, "deposit");

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

    @Test
    void balanceIsCorrectAfterSingleDeposit() {
        SavingsAccount savingsAccount = new SavingsAccount(regularCustomer, 1000);
        CheckingAccount checkingAccount = new CheckingAccount(premiumCustomer, 500);

        savingsAccount.processTransaction(250.50, "deposit");
        checkingAccount.processTransaction(100.25, "deposit");

        assertEquals(1250.50, savingsAccount.getBalance(), 0.01);
        assertEquals(600.25, checkingAccount.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterMultipleDeposits() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);

        account.processTransaction(100, "deposit");
        assertEquals(1100, account.getBalance(), 0.01);

        account.processTransaction(250, "deposit");
        assertEquals(1350, account.getBalance(), 0.01);

        account.processTransaction(50.75, "deposit");
        assertEquals(1400.75, account.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterMultipleDepositsForCheckingAccount() {
        CheckingAccount account = new CheckingAccount(premiumCustomer, 500);

        account.processTransaction(200, "deposit");
        assertEquals(700, account.getBalance(), 0.01);

        account.processTransaction(150, "deposit");
        assertEquals(850, account.getBalance(), 0.01);

        account.processTransaction(50, "deposit");
        assertEquals(900, account.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterLargeDeposit() {
        SavingsAccount savingsAccount = new SavingsAccount(regularCustomer, 1000);
        CheckingAccount checkingAccount = new CheckingAccount(premiumCustomer, 500);

        savingsAccount.processTransaction(10000, "deposit");
        checkingAccount.processTransaction(25000, "deposit");

        assertEquals(11000, savingsAccount.getBalance(), 0.01);
        assertEquals(25500, checkingAccount.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterSmallDecimalDeposit() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 100);

        account.processTransaction(0.01, "deposit");
        assertEquals(100.01, account.getBalance(), 0.001);

        account.processTransaction(0.99, "deposit");
        assertEquals(101.00, account.getBalance(), 0.001);

        account.processTransaction(1.50, "deposit");
        assertEquals(102.50, account.getBalance(), 0.001);
    }

    @Test
    void balanceRemainsUnchangedAfterFailedDeposit() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);
        double initialBalance = account.getBalance();
        account.processTransaction(0, "deposit");
        assertEquals(initialBalance, account.getBalance(), 0.01);
        account.processTransaction(-100, "deposit");
        assertEquals(initialBalance, account.getBalance(), 0.01);
    }
}

