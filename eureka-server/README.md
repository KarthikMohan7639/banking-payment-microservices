# Eureka Server Module (Port 8761)

## Overview & Purpose
The `eureka-server` module acts as the **Service Registry & Discovery Server** for the Banking Microservices architecture. In a distributed microservice ecosystem, services need to locate each other dynamically without hardcoding IP addresses or hostnames.

## Key Design Choices & Annotations
- `@EnableEurekaServer`: Configures Spring Boot to run an embedded Netflix Eureka registry.
- `eureka.client.register-with-eureka: false`: Prevents the Eureka Server itself from attempting to register as a client in its own registry.
- `eureka.client.fetch-registry: false`: Disables registry fetching since Eureka Server holds the single source of truth locally.

## Why Eureka is Needed in Banking Microservices
1. **Dynamic Scaling:** Microservice instances can scale up/down dynamically; client services discover instances via client-side load balancing (`lb://SERVICE-NAME`).
2. **High Availability & Fault Tolerance:** If a service instance fails health checks, Eureka removes it from the active service registry, preventing downstream traffic failures.
3. **Decoupling Network Infrastructure:** Services communicate using logical application names (e.g., `account-service`, `payment-service`) rather than physical hostnames or ports.
