# Banking Payment Microservices Project

A production-ready, multi-module Spring Boot 3.3.4 microservices architecture built for BFSI (Banking, Financial Services, and Insurance) technical interview demonstrations.

---

## 🏛 Architecture Diagram

```
                              ┌────────────────────────┐
                              │     Client / Postman   │
                              └───────────┬────────────┘
                                          │
                                          ▼
                              ┌────────────────────────┐
                              │   API Gateway (8080)   │ (Spring Cloud Gateway + JWT Filter + Rate Limiting)
                              └───────────┬────────────┘
                                          │
           ┌──────────────────────────────┼──────────────────────────────┬──────────────────────────────┐
           │                              │                              │                              │
           ▼                              ▼                              ▼                              ▼
┌────────────────────┐         ┌────────────────────┐         ┌────────────────────┐         ┌────────────────────┐
│  Account Service   │         │  Payment Service   │         │  Payment Gateway   │         │Notification Service│
│     (Port 8081)    │         │    (Port 8082)     │         │    (Port 8084)     │         │    (Port 8083)     │
└──────────┬─────────┘         └──────────┬─────────┘         └────────────────────┘         └──────────┬─────────┘
           │                              │ (OpenFeign)                                                 │
           │                              ├──────────────────────────────►                              │
           │                              │ (OpenFeign)                                                 │
           │                              ├─────────────────────────────────────────────────────────────┘
           │                              │
           │                              │ (Kafka Event: payment-events)
           │                              ▼
           │                   ┌────────────────────┐
           └──────────────────►│ Eureka Service Reg │ (Port 8761)
                               └────────────────────┘
```

---

## 📦 Service Matrix & Ports

| Module | Port | Technology Stack | Responsibility |
|---|---|---|---|
| `eureka-server` | 8761 | Spring Cloud Netflix Eureka | Centralized Service Registry & Discovery |
| `account-service` | 8081 | Spring Data JPA, JJWT, H2, Spring Security | Account CRUD, balance debit/credit, JWT authentication, SQL analytics |
| `payment-service` | 8082 | OpenFeign, Resilience4j, Kafka, Spring Data JPA | SAGA Orchestrator for money transfers, compensation, Kafka event publishing |
| `payment-gateway` | 8084 | Spring Data JPA, H2, Idempotency engine | Simulated external PSP (Stripe/Razorpay style) with idempotency enforcement |
| `notification-service`| 8083 | Spring Kafka, H2 | Consumes payment events, logs notifications, provides lookup API |
| `api-gateway` | 8080 | Spring Cloud Gateway, Reactive JWT Filter | Edge routing, JWT validation, rate-limiting |

---

## 🛠 Tech Stack & Versions

- **Java Version:** 17
- **Spring Boot:** 3.3.4
- **Spring Cloud:** 2023.0.3
- **Database:** H2 In-Memory DB (per service)
- **Security:** JJWT 0.11.5 + Spring Security
- **Inter-Service Communication:** OpenFeign
- **Resilience:** Resilience4j CircuitBreaker
- **Messaging:** Spring Kafka (Embedded Kafka for testing)
- **Build Tool:** Maven multi-module

---

## 🚀 Startup Order

Services should be started sequentially in the following order:
1. **`eureka-server`** (Port 8761)
2. **`account-service`** (Port 8081)
3. **`payment-gateway`** (Port 8084)
4. **`payment-service`** (Port 8082)
5. **`notification-service`** (Port 8083)
6. **`api-gateway`** (Port 8080)

---

## 🧪 End-to-End Postman / Curl Walkthrough

### Step 1: Login & Obtain JWT Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```
*Response:* Copy the returned JWT token.

### Step 2: Create Two Accounts
```bash
# Create Account 1
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Sender User","balance":1000.00,"ownerUsername":"john_doe"}'

# Create Account 2
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Receiver User","balance":500.00,"ownerUsername":"receiver_user"}'
```

### Step 3: Initiate Payment with Idempotency Key
```bash
curl -X POST http://localhost:8080/payments \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":200.00,"currency":"USD","idempotencyKey":"IDEM-KEY-001"}'
```

### Step 4: Retry Payment with SAME Idempotency Key
```bash
curl -X POST http://localhost:8080/payments \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":200.00,"currency":"USD","idempotencyKey":"IDEM-KEY-001"}'
```
*Note:* Observe that the exact same payment object is returned without double debiting Account 1.

---

## 📊 SQL Analytics & Optimization Section

### 1. Second Highest Salary Query
```sql
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
LIMIT 1 OFFSET 1;
```

### 2. Top 3 Customers by Payment Volume (Window Function)
```sql
SELECT customer_name, total_amount, rnk FROM (
    SELECT customer_name, SUM(amount) AS total_amount,
           ROW_NUMBER() OVER (ORDER BY SUM(amount) DESC) AS rnk
    FROM payment_log
    GROUP BY customer_name
) ranked
WHERE rnk <= 3;
```

### 3. Recommended Database Indexes
- **`CREATE INDEX idx_paymentlog_customer_amount ON payment_log(customer_name, amount);`**
  - *Why:* Optimizes GROUP BY aggregation and SUM() computation over large payment log tables.
- **`CREATE INDEX idx_paymentlog_created_at ON payment_log(created_at);`**
  - *Why:* Speeds up time-series range filtering and analytics aggregations across date intervals.
