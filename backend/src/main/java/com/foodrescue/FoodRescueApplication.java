package com.foodrescue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Smart Food Rescue & Redistribution System backend.
 *
 * Annotations:
 *  @SpringBootApplication — enables auto-configuration, component scanning, and configuration.
 *  @EnableCaching         — activates Spring Cache (backed by Redis).
 *  @EnableScheduling      — enables @Scheduled methods (e.g., expiry checker).
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class FoodRescueApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodRescueApplication.class, args);
    }
}
