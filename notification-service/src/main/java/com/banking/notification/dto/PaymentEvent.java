package com.banking.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the payment event received from Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private String paymentId;
    private String status;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String reason;
    private LocalDateTime eventTime;
}
