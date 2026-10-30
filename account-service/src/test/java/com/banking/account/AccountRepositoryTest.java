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

/**
 * Data JPA test class validating native SQL queries against H2 database.
 */
// WHY: Configures an in-memory database and Spring Data JPA components for repository testing.
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
        assertEquals(3, top3.size());
        assertEquals("Alice Smith", top3.get(0)[0]);
    }
}
