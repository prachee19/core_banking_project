package com.dbms.core_banking.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BigDecimal getBalanceForUpdate(Integer accountId) {

        String sql = """
                SELECT balance
                FROM Accounts
                WHERE account_id = ?
                FOR UPDATE
                """;

        return jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                accountId
        );
    }

    public int updateBalance(Integer accountId, BigDecimal amount) {

        String sql = """
                UPDATE Accounts
                SET balance = balance + ?
                WHERE account_id = ?
                """;

        return jdbcTemplate.update(sql, amount, accountId);
    }

    public boolean accountExists(Integer accountId) {

        String sql = """
                SELECT COUNT(*)
                FROM Accounts
                WHERE account_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                accountId
        );

        return count != null && count > 0;
    }

    public Map<String, Object> getAccount(Integer accountId) {

        String sql = """
                SELECT account_id, customer_id, account_type,
                       balance, status, created_at
                FROM Accounts
                WHERE account_id = ?
                """;

        return jdbcTemplate.queryForMap(sql, accountId);
    }

    public List<Map<String, Object>> getTransactions(Integer accountId) {

        String sql = """
                SELECT transaction_id, account_id, amount,
                       transaction_type, transaction_date
                FROM Transactions
                WHERE account_id = ?
                ORDER BY transaction_date DESC
                """;

        return jdbcTemplate.queryForList(sql, accountId);
    }
}
