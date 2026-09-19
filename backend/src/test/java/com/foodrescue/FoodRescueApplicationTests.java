package com.foodrescue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic smoke test — verifies the Spring application context loads without errors.
 *
 * Uses H2 in-memory database (PostgreSQL NOT required for tests).
 * Redis is replaced with simple in-memory cache.
 * Scheduling is disabled to prevent scheduler from running during tests.
 * SQL init is disabled (H2 doesn't need the PostgreSQL seed data).
 */
@SpringBootTest
@TestPropertySource(properties = {
        // H2 in-memory database (replaces PostgreSQL)
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",

        // Use simple in-memory cache (no Redis needed)
        "spring.cache.type=simple",

        // Don't run data.sql in tests
        "spring.sql.init.mode=never",

        // Disable scheduling to avoid scheduler errors in tests
        "spring.task.scheduling.pool.size=0",

        // JWT secret for tests
        "app.jwt.secret=TestSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024",
        "app.jwt.expiration-ms=86400000",

        // CORS
        "app.cors.allowed-origins=http://localhost:3000"
})
class FoodRescueApplicationTests {

    @Test
    void contextLoads() {
        // If this test passes, the entire Spring context starts without errors.
        // This validates: beans, JPA, Security, Cache configuration, etc.
        System.out.println("✅ Spring context loaded successfully!");
    }
}
