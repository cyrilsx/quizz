package com.quizzplatform.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExampleTestContainerTest extends TestContainerBase {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource);
        
        // Test that we can get a connection to the PostgreSQL container
        try (var connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            
            // Test basic SQL query
            var jdbcTemplate = new JdbcTemplate(dataSource);
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            assertEquals(1, result);
        }
    }

    @Test
    void testContainerIsRunning() {
        assertNotNull(getPostgresContainer());
        assertTrue(getPostgresContainer().isRunning());
    }
}