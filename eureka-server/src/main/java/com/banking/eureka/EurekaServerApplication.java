package com.banking.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main application class for the Eureka Service Registry.
 *
 * Annotations used:
 * - @SpringBootApplication: Convenience annotation combining @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * - @EnableEurekaServer: Activates the Eureka Service Registry configuration and embedded UI dashboard at port 8761.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
