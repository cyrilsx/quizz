package com.quizzplatform.integration;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {
    
    private static PostgreSQLContainer<?> postgresContainer;
    
    static {
        try {
            postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("quizz_test")
                    .withUsername("testuser")
                    .withPassword("testpass");
            postgresContainer.start();
            System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
            System.setProperty("spring.datasource.username", postgresContainer.getUsername());
            System.setProperty("spring.datasource.password", postgresContainer.getPassword());
            System.setProperty("spring.datasource.driver-class-name", postgresContainer.getDriverClassName());
            System.out.println("PostgreSQL Testcontainer started successfully");
        } catch (Exception e) {
            System.err.println("Failed to start PostgreSQL Testcontainer: " + e.getMessage());
            System.err.println("Docker may not be available. Using H2 database for testing.");
            // Fallback to H2 database configuration
            System.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            System.setProperty("spring.datasource.username", "sa");
            System.setProperty("spring.datasource.password", "");
            System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        }
    }
    
    public static PostgreSQLContainer<?> getPostgresContainer() {
        return postgresContainer;
    }
}