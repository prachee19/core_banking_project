package com.dbms.core_banking.service;

import com.dbms.core_banking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void deposit(Integer accountId, BigDecimal amount) {

        validateAmount(amount);

        if (!accountRepository.accountExists(accountId)) {
            throw new IllegalArgumentException("Account not found");
        }

        accountRepository.getBalanceForUpdate(accountId);
        accountRepository.updateBalance(accountId, amount);
    }

    @Transactional
    public void withdraw(Integer accountId, BigDecimal amount) {

        validateAmount(amount);

        if (!accountRepository.accountExists(accountId)) {
            throw new IllegalArgumentException("Account not found");
        }

        BigDecimal balance =
                accountRepository.getBalanceForUpdate(accountId);

        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        accountRepository.updateBalance(accountId, amount.negate());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }
    }
}
