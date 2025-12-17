package test.java.models;

import com.bam.models.PremiumCustomer;
import com.bam.models.RegularCustomer;
import org.junit.jupiter.api.BeforeEach;

public abstract class AccountTestBase {
    protected RegularCustomer regularCustomer;
    protected PremiumCustomer premiumCustomer;

    @BeforeEach
    void setUpCustomers() {
        regularCustomer = new RegularCustomer("Alice", 30, "1234567890", "alice@test.com", "123 Street");
        premiumCustomer = new PremiumCustomer("Bob", 40, "0987654321", "bob@test.com", "456 Avenue");
    }
}
