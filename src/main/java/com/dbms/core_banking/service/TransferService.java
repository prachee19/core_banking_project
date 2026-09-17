package com.dbms.core_banking.service;

import com.dbms.core_banking.repository.AccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;

    public TransferService(AccountRepository accountRepository, JdbcTemplate jdbcTemplate) {
        this.accountRepository = accountRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String transfer(Integer senderAccountId,
                           Integer receiverAccountId,
                           BigDecimal amount) {

        if (senderAccountId == null || receiverAccountId == null) {
            throw new IllegalArgumentException("Account IDs are required");
        }

        if (senderAccountId.equals(receiverAccountId)) {
            throw new IllegalArgumentException("Sender and receiver accounts must be different");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        if (!accountRepository.accountExists(senderAccountId)) {
            throw new IllegalArgumentException("Sender account not found");
        }

        if (!accountRepository.accountExists(receiverAccountId)) {
            throw new IllegalArgumentException("Receiver account not found");
        }

        BigDecimal senderBalance;

        if (senderAccountId < receiverAccountId) {
            senderBalance = accountRepository.getBalanceForUpdate(senderAccountId);
            accountRepository.getBalanceForUpdate(receiverAccountId);
        } else {
            accountRepository.getBalanceForUpdate(receiverAccountId);
            senderBalance = accountRepository.getBalanceForUpdate(senderAccountId);
        }

        if (senderBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        accountRepository.updateBalance(senderAccountId, amount.negate());
        accountRepository.updateBalance(receiverAccountId, amount);

        jdbcTemplate.update(
                """
                INSERT INTO Transactions
                (account_id, amount, transaction_type)
                VALUES (?, ?, 'DEBIT')
                """,
                senderAccountId,
                amount
        );

        jdbcTemplate.update(
                """
                INSERT INTO Transactions
                (account_id, amount, transaction_type)
                VALUES (?, ?, 'CREDIT')
                """,
                receiverAccountId,
                amount
        );

        String referenceNumber =
                "TXN-" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 20);

        jdbcTemplate.update(
                """
                INSERT INTO Transfers
                (reference_number, sender_account_id, receiver_account_id, amount, status)
                VALUES (?, ?, ?, ?, 'SUCCESS')
                """,
                referenceNumber,
                senderAccountId,
                receiverAccountId,
                amount
        );

        jdbcTemplate.update(
                """
                INSERT INTO Audit_Logs
                (action, entity_type, entity_id, description)
                VALUES (?, ?, ?, ?)
                """,
                "MONEY_TRANSFER",
                "TRANSFER",
                referenceNumber,
                "Transfer from account " + senderAccountId
                        + " to account " + receiverAccountId
                        + " for amount " + amount
        );

        return referenceNumber;
    }
}
