package com.banking.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing payment transaction history for native SQL window function analytics.
 */
// WHY: Maps domain class to 'payment_log' table.
@Entity
@Table(name = "payment_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLog {

    // WHY: Primary key identifier.
    @Id
    // WHY: Identity sequence primary key generation.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
