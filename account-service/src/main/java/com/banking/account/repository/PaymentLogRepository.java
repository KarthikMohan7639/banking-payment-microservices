package com.banking.account.repository;

import com.banking.account.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

    /**
     * Native SQL Query demonstrating ANSI SQL Window Functions (ROW_NUMBER() OVER).
     * Calculates the top 3 customers by cumulative payment amount across payment logs.
     */
    @Query(value = """
            SELECT customer_name, total_amount, rnk FROM (
                SELECT customer_name, SUM(amount) AS total_amount,
                       ROW_NUMBER() OVER (ORDER BY SUM(amount) DESC) AS rnk
                FROM payment_log
                GROUP BY customer_name
            ) ranked
            WHERE rnk <= 3
            """, nativeQuery = true)
    List<Object[]> findTop3CustomersByPaymentAmountNative();
}
