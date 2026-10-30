package com.banking.notification;

import com.banking.notification.dto.PaymentEvent;
import com.banking.notification.entity.NotificationLog;
import com.banking.notification.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Embedded Kafka integration test verifying event consumption in notification-service.
 */
// WHY: Boots full Spring context alongside an in-memory Kafka broker for embedded integration testing.
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"payment-events"})
@DirtiesContext
@TestPropertySource(properties = {
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
class NotificationConsumerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private NotificationLogRepository repository;

    @Test
    void testKafkaListenerConsumesPaymentEvent() {
        PaymentEvent event = PaymentEvent.builder()
                .paymentId("PAY-EMBEDDED-100")
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(new BigDecimal("250.00"))
                .status("SUCCESS")
                .reason(null)
                .eventTime(LocalDateTime.now())
                .build();

        kafkaTemplate.send("payment-events", event.getPaymentId(), event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            List<NotificationLog> logs = repository.findByPaymentId("PAY-EMBEDDED-100");
            assertFalse(logs.isEmpty());
            assertEquals("SUCCESS", logs.get(0).getStatus());
            assertEquals(new BigDecimal("250.00"), logs.get(0).getAmount());
        });
    }
}
