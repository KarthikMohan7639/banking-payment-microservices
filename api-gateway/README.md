# API Gateway Module (Port 8080)

## Overview & Role
The `api-gateway` serves as the **Single Point of Entry (Edge Service)** for external client requests in the microservices ecosystem. Built on **Spring Cloud Gateway (Reactive Netty Stack)**, it provides client request routing, edge-level security, and request header transformation.

## Key Features & Implementations
1. **Eureka Client-Side Routing:** Uses logical service names (`lb://ACCOUNT-SERVICE`, `lb://PAYMENT-SERVICE`, etc.) resolved dynamically via Eureka Service Registry.
2. **Reactive `JwtAuthenticationFilter`:** Implements `GatewayFilter` operating on `ServerWebExchange`. Intercepts incoming requests, validates Bearer tokens via JJWT, and injects authenticated user headers (`X-Auth-Username`, `X-Auth-Role`) for downstream microservices.
3. **Public Route Permitting:** `/auth/**` paths bypass JWT token checks to allow initial client logins.
