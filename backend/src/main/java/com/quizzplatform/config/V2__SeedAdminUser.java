package com.quizzplatform.config;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class V2__SeedAdminUser extends BaseJavaMigration {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    @Override
    public void migrate(Context context) throws Exception {
        if (adminPassword == null || adminPassword.isBlank()) {
            if (adminExists(context)) {
                return;
            }
            System.out.println("Skipping admin user creation: ADMIN_PASSWORD is not set");
            return;
        }

        String existingHash = findPasswordHash(context);
        if (existingHash != null) {
            if (!passwordEncoder.matches(adminPassword, existingHash)) {
                try (PreparedStatement update = context.getConnection().prepareStatement(
                        "UPDATE users SET email = ?, password = ? WHERE username = ?")) {
                    update.setString(1, adminEmail);
                    update.setString(2, passwordEncoder.encode(adminPassword));
                    update.setString(3, adminUsername);
                    update.executeUpdate();
                }
            }
            return;
        }

        try (PreparedStatement insert = context.getConnection().prepareStatement(
                "INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)")) {
            insert.setString(1, adminUsername);
            insert.setString(2, adminEmail);
            insert.setString(3, passwordEncoder.encode(adminPassword));
            insert.executeUpdate();
        }
    }

    private boolean adminExists(Context context) throws Exception {
        return findPasswordHash(context) != null;
    }

    private String findPasswordHash(Context context) throws Exception {
        try (PreparedStatement select = context.getConnection().prepareStatement(
                "SELECT password FROM users WHERE username = ?")) {
            select.setString(1, adminUsername);
            try (ResultSet rs = select.executeQuery()) {
                return rs.next() ? rs.getString("password") : null;
            }
        }
    }
}
