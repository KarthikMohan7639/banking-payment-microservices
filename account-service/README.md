# Account Service Module (Port 8081)

## Overview
The `account-service` is responsible for handling user accounts, managing monetary balances (debit/credit), providing JWT security endpoints (`/auth/login`), enforcing role-based authorization (e.g. `@PreAuthorize("hasRole('ADMIN')")`), and executing native SQL queries for business analytics.

## Key Annotations & Design Choices
- `@Transactional`: Applied on `debit()` and `credit()` methods. Guarantees ACID transaction semantics—if balance updates fail or throw runtime exceptions, state modifications rollback cleanly.
- `@PreAuthorize("hasRole('ADMIN')")`: Used on administrative endpoints (e.g., `DELETE /accounts/{id}`) to enforce method-level RBAC.
- `@RestControllerAdvice`: Centralizes HTTP exception mapping (`GlobalExceptionHandler`). Converts exceptions like `InsufficientBalanceException` into standard JSON responses with status code 422 (Unprocessable Entity).
- `OncePerRequestFilter`: `JwtAuthFilter` extracts and validates JJWT tokens from the `Authorization: Bearer <token>` header on incoming requests.

## SQL Query Demos (Interview Depth)
1. **Second Highest Salary:** Native SQL query using `ORDER BY ... LIMIT 1 OFFSET 1` to find the second highest employee salary.
2. **Top 3 Customers by Payment Volume:** Native SQL window function using `ROW_NUMBER() OVER (ORDER BY SUM(amount) DESC)` over the `payment_log` table.
