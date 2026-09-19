package com.dbms.core_banking.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> findByUsername(String username) {
        return jdbcTemplate.queryForMap("""
                SELECT user_id, username, password, role, customer_id
                FROM Users
                WHERE username = ?
                """, username);
    }

    public Integer getCustomerIdByUsername(String username) {
        return jdbcTemplate.queryForObject("""
                SELECT customer_id
                FROM Users
                WHERE username = ?
                """, Integer.class, username);
    }
}
