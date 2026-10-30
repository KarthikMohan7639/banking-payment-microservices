package com.banking.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main application class for the Eureka Service Registry module.
 */
// WHY: Bootstrap Spring Boot application context and enable autoconfiguration.
@SpringBootApplication
// WHY: Activates Netflix Eureka Service Registry server on port 8761 for dynamic service discovery.
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
