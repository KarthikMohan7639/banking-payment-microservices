package com.banking.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the Account Service module.
 */
// WHY: Bootstraps Spring Boot application context and component scanning.
@SpringBootApplication
// WHY: Registers this service with Eureka Service Registry for client-side discovery.
@EnableDiscoveryClient
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
