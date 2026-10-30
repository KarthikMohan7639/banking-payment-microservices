# Notification Service Module (Port 8083)

## Overview & Role
The `notification-service` is an asynchronous event-driven consumer module. It listens to the `payment-events` Kafka topic, processes payment status updates (e.g. `SUCCESS`, `FAILED`), and records audit logs into its local H2 database.

## Key Design Patterns & Annotations
- `@KafkaListener`: Subscribes to `payment-events` topic using group ID `notification-group`.
- `@EmbeddedKafka`: Used in integration tests (`NotificationConsumerTest`) to spin up an in-memory Kafka broker so tests execute without needing an external Kafka broker running on the local host.
- `ErrorHandlingDeserializer` + `JsonDeserializer`: Configured in `application.yml` to safely deserialize incoming JSON events into Java `PaymentEvent` POJOs without throwing unhandled deserialization crashes.
