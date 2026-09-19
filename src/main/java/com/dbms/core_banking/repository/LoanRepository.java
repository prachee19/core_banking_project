package com.dbms.core_banking.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Repository
public class LoanRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createLoan(Integer customerId, BigDecimal loanAmount,
                          BigDecimal interestRate, Date startDate, Date endDate) {
        return jdbcTemplate.update("""
                INSERT INTO Loans
                (customer_id, loan_amount, interest_rate, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, 'PENDING')
                """, customerId, loanAmount, interestRate, startDate, endDate);
    }

    public Map<String, Object> getLoan(Integer loanId) {
        return jdbcTemplate.queryForMap("""
                SELECT loan_id, customer_id, loan_amount, interest_rate,
                       start_date, end_date, status
                FROM Loans WHERE loan_id = ?
                """, loanId);
    }

    public List<Map<String, Object>> getLoansByCustomer(Integer customerId) {
        return jdbcTemplate.queryForList("""
                SELECT loan_id, customer_id, loan_amount, interest_rate,
                       start_date, end_date, status
                FROM Loans WHERE customer_id = ?
                ORDER BY loan_id DESC
                """, customerId);
    }

    public List<Map<String, Object>> getPendingLoans() {
        return jdbcTemplate.queryForList("""
                SELECT loan_id, customer_id, loan_amount, interest_rate,
                       start_date, end_date, status
                FROM Loans WHERE status = 'PENDING'
                ORDER BY loan_id ASC
                """);
    }

    public int updateLoanStatus(Integer loanId, String status) {
        return jdbcTemplate.update(
                "UPDATE Loans SET status = ? WHERE loan_id = ?",
                status, loanId);
    }

    public boolean loanExists(Integer loanId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Loans WHERE loan_id = ?",
                Integer.class, loanId);
        return count != null && count > 0;
    }

    public Integer getCustomerId(Integer loanId) {
        return jdbcTemplate.queryForObject(
                "SELECT customer_id FROM Loans WHERE loan_id = ?",
                Integer.class, loanId);
    }

    public BigDecimal getLoanAmount(Integer loanId) {
        return jdbcTemplate.queryForObject(
                "SELECT loan_amount FROM Loans WHERE loan_id = ?",
                BigDecimal.class, loanId);
    }

    public BigDecimal getTotalPaid(Integer loanId) {
        return jdbcTemplate.queryForObject("""
                SELECT COALESCE(SUM(amount_paid), 0)
                FROM Loan_Payments WHERE loan_id = ?
                """, BigDecimal.class, loanId);
    }

    public int addPayment(Integer loanId, BigDecimal amountPaid) {
        return jdbcTemplate.update("""
                INSERT INTO Loan_Payments
                (loan_id, payment_date, amount_paid)
                VALUES (?, CURRENT_DATE, ?)
                """, loanId, amountPaid);
    }

    public List<Map<String, Object>> getPayments(Integer loanId) {
        return jdbcTemplate.queryForList("""
                SELECT payment_id, loan_id, payment_date, amount_paid
                FROM Loan_Payments
                WHERE loan_id = ?
                ORDER BY payment_date DESC, payment_id DESC
                """, loanId);
    }
}
