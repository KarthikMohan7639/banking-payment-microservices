package com.banking.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain Event emitted to Kafka topic upon payment processing completion or failure.
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
