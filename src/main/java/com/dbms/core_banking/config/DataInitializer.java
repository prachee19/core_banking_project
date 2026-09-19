package com.dbms.core_banking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {

        return args -> {

            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM Users WHERE username = ?",
                    Integer.class,
                    "admin"
            );

            if (count == null || count == 0) {
                jdbcTemplate.update("""
                        INSERT INTO Users
                        (username, password, role, customer_id)
                        VALUES (?, ?, ?, NULL)
                        """,
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "ADMIN"
                );
            }

            count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM Users WHERE username = ?",
                    Integer.class,
                    "customer1"
            );

            if (count == null || count == 0) {
                jdbcTemplate.update("""
                        INSERT INTO Users
                        (username, password, role, customer_id)
                        VALUES (?, ?, ?, ?)
                        """,
                        "customer1",
                        passwordEncoder.encode("customer123"),
                        "CUSTOMER",
                        1
                );
            }

            count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM Users WHERE username = ?",
                    Integer.class,
                    "customer2"
            );

            if (count == null || count == 0) {
                jdbcTemplate.update("""
                        INSERT INTO Users
                        (username, password, role, customer_id)
                        VALUES (?, ?, ?, ?)
                        """,
                        "customer2",
                        passwordEncoder.encode("customer123"),
                        "CUSTOMER",
                        2
                );
            }

            System.out.println("Database users initialized successfully.");
        };
    }
}
