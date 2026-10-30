package com.banking.notification.consumer;

import com.banking.notification.dto.PaymentEvent;
import com.banking.notification.entity.NotificationLog;
import com.banking.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka event consumer listening to payment domain events and logging notifications.
 */
// WHY: Registers class as a Spring component bean.
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final NotificationLogRepository repository;

    // WHY: Subscribes method as a listener to 'payment-events' Kafka topic under consumer group 'notification-group'.
    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Received PaymentEvent for paymentId: {}, status: {}, reason: {}",
                event.getPaymentId(), event.getStatus(), event.getReason());

        NotificationLog logEntry = NotificationLog.builder()
                .paymentId(event.getPaymentId())
                .fromAccountId(event.getFromAccountId())
                .toAccountId(event.getToAccountId())
                .amount(event.getAmount())
                .status(event.getStatus())
                .build();

        repository.save(logEntry);
        log.info("Successfully persisted notification log entry for paymentId: {}", event.getPaymentId());
    }
}
