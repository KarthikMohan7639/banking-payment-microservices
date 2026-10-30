package com.banking.account;

import com.banking.account.repository.EmployeeRepository;
import com.banking.account.repository.PaymentLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PaymentLogRepository paymentLogRepository;

    @Test
    void testSecondHighestSalaryQueryWithSeedData() {
        Optional<Double> secondHighest = employeeRepository.findSecondHighestSalary();
        assertTrue(secondHighest.isPresent());
        assertEquals(110000.0, secondHighest.get());
    }

    @Test
    void testTop3CustomersNativeQueryWithSeedData() {
        List<Object[]> top3 = paymentLogRepository.findTop3CustomersByPaymentAmountNative();
        for (Object[] row : top3) {
            System.out.println("Customer: " + row[0] + ", Total: " + row[1] + ", Rank: " + row[2]);
        }
        assertEquals(3, top3.size());
        assertEquals("Alice Smith", top3.get(0)[0]);
    }
}
