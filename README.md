# Banking Payment Microservices Project

A production-ready, multi-module Spring Boot 3.3.4 microservices architecture built for BFSI (Banking, Financial Services, and Insurance) technical interview demonstrations.

---

## 🏛 Architecture Diagram

```
                              ┌────────────────────────┐
                              │      Client / UI       │
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

## 🚀 How to Run Locally

### Prerequisites
- JDK 17
- Maven 3.8+

### Step 1: Build the Entire Project
```bash
mvn clean package
```

### Step 2: Start Services in Order
1. **Eureka Server:** `java -jar eureka-server/target/eureka-server-1.0.0-SNAPSHOT.jar`
2. **Account Service:** `java -jar account-service/target/account-service-1.0.0-SNAPSHOT.jar`
3. **Payment Gateway:** `java -jar payment-gateway/target/payment-gateway-1.0.0-SNAPSHOT.jar`
4. **Payment Service:** `java -jar payment-service/target/payment-service-1.0.0-SNAPSHOT.jar`
5. **Notification Service:** `java -jar notification-service/target/notification-service-1.0.0-SNAPSHOT.jar`
6. **API Gateway:** `java -jar api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar`

---

## 🔑 Endpoints Overview

- **Eureka Dashboard:** `http://localhost:8761`
- **Login / Authentication:** `POST http://localhost:8080/auth/login`
- **Account Operations:** `http://localhost:8080/accounts`
- **Execute Payment:** `POST http://localhost:8080/payments`
- **Gateway Charge:** `POST http://localhost:8080/gateway/charge`
- **Notifications:** `GET http://localhost:8080/notifications`
