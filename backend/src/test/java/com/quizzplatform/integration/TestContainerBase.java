package com.quizzplatform.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class TestContainerBase {
    
    private static final PostgreSQLContainer<?> postgresContainer = 
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                    .withDatabaseName("quizz_test")
                    .withUsername("testuser")
                    .withPassword("testpass");
    
    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }
    
    @AfterAll
    static void afterAll() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            postgresContainer.stop();
        }
    }
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL properties
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
    }
    
    public static PostgreSQLContainer<?> getPostgresContainer() {
        return postgresContainer;
    }
}