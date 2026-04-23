package com.quizzplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Local application runner using Testcontainers PostgreSQL.
 * Start this class to run the application with an embedded PostgreSQL container.
 */
public class LocalQuizzApp {

    private static final PostgreSQLContainer<?> postgres = 
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("quizzdb")
                    .withUsername("quizzuser")
                    .withPassword("quizzpass");

    public static void main(String[] args) {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.datasource.driver-class-name", postgres.getDriverClassName());
        
        System.out.println("PostgreSQL Testcontainer started");
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("Starting QuizzBackendApplication...");
        
        SpringApplication.run(QuizzBackendApplication.class, args);
    }
}
