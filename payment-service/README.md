# Payment Service Module (Port 8082)

## Overview & Role
The `payment-service` acts as the **SAGA Orchestrator** for money transfers between accounts. It coordinates cross-service HTTP calls using OpenFeign, enforces fault tolerance via Resilience4j, and publishes event messages to Kafka (`payment-events`).

## Key Technical Concepts
1. **SAGA Pattern with Compensating Transactions:**
   - **Step 1:** Debit source account via `account-service`.
   - **Step 2:** Charge external PSP via `payment-gateway`. If failed/declined, execute compensation: refund source account.
   - **Step 3:** Credit destination account via `account-service`. If failed, execute compensation: refund source account.
2. **`@Transactional(rollbackFor = Exception.class)`:** Ensures local database mutations (saving Payment record) commit/rollback atomically.
3. **Resilience4j Circuit Breaker:** Applied on `processPayment()` with a fallback method (`processPaymentFallback`) when gateway or downstream calls fail repeatedly.
4. **Kafka Producer:** Emits `PaymentEvent` to the `payment-events` topic upon transaction completion.
