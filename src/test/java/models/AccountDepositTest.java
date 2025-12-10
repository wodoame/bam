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

    @Test
    void balanceIsCorrectAfterSingleDeposit() {
        SavingsAccount savingsAccount = new SavingsAccount(regularCustomer, 1000);
        CheckingAccount checkingAccount = new CheckingAccount(premiumCustomer, 500);

        savingsAccount.deposit(250.50);
        checkingAccount.deposit(100.25);

        assertEquals(1250.50, savingsAccount.getBalance(), 0.01);
        assertEquals(600.25, checkingAccount.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterMultipleDeposits() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);

        account.deposit(100);
        assertEquals(1100, account.getBalance(), 0.01);

        account.deposit(250);
        assertEquals(1350, account.getBalance(), 0.01);

        account.deposit(50.75);
        assertEquals(1400.75, account.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterMultipleDepositsForCheckingAccount() {
        CheckingAccount account = new CheckingAccount(premiumCustomer, 500);

        account.deposit(200);
        assertEquals(700, account.getBalance(), 0.01);

        account.deposit(150);
        assertEquals(850, account.getBalance(), 0.01);

        account.deposit(50);
        assertEquals(900, account.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterLargeDeposit() {
        SavingsAccount savingsAccount = new SavingsAccount(regularCustomer, 1000);
        CheckingAccount checkingAccount = new CheckingAccount(premiumCustomer, 500);

        savingsAccount.deposit(10000);
        checkingAccount.deposit(25000);

        assertEquals(11000, savingsAccount.getBalance(), 0.01);
        assertEquals(25500, checkingAccount.getBalance(), 0.01);
    }

    @Test
    void balanceIsCorrectAfterSmallDecimalDeposit() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 100);

        account.deposit(0.01);
        assertEquals(100.01, account.getBalance(), 0.001);

        account.deposit(0.99);
        assertEquals(101.00, account.getBalance(), 0.001);

        account.deposit(1.50);
        assertEquals(102.50, account.getBalance(), 0.001);
    }

    @Test
    void balanceRemainsUnchangedAfterFailedDeposit() {
        SavingsAccount account = new SavingsAccount(regularCustomer, 1000);
        double initialBalance = account.getBalance();

        try {
            account.deposit(0);
        } catch (InvalidDepositAmountException e) {
            // Expected exception
        }

        assertEquals(initialBalance, account.getBalance(), 0.01);

        try {
            account.deposit(-100);
        } catch (InvalidDepositAmountException e) {
            // Expected exception
        }

        assertEquals(initialBalance, account.getBalance(), 0.01);
    }
}

