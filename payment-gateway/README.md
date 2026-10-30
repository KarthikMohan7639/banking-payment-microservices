# Payment Gateway Module (Port 8084)

## Overview & Role
The `payment-gateway` simulates an **External Payment Service Provider (PSP)** such as Stripe or Razorpay. It mimics the "money leaves the bank" step in inter-bank transfer systems.

## Why Idempotency Keys are Critical in Banking / PSP Integrations
In distributed financial networks, network timeouts, retries, and dropped packets are common. If a caller submits a payment charge request and experiences a network timeout before receiving a response, it may retry the request.
- **Without Idempotency:** The customer is double-charged (money leaves twice).
- **With Idempotency:** The PSP checks the unique `idempotencyKey`. If present, it returns the existing transaction result immediately without processing a duplicate charge.

## Handling Real-World Failure Modes
In real BFSI systems, a charge might succeed on the external PSP side, but the downstream DB write in the calling service could fail due to network partitioning or process crash.
1. **Reconciliation:** Background cron jobs query PSP endpoints (`GET /gateway/transactions/{gatewayRef}`) to reconcile orphaned transactions.
2. **SAGA Compensating Transactions:** If downstream processing fails after a successful PSP charge, a compensating refund request is issued back to the PSP to reverse the funds.
