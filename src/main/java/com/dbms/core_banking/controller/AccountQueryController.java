package com.dbms.core_banking.controller;

import com.dbms.core_banking.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountQueryController {

    private final AccountRepository accountRepository;

    public AccountQueryController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Integer accountId) {

        if (!accountRepository.accountExists(accountId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                accountRepository.getAccount(accountId)
        );
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(
            @PathVariable Integer accountId) {

        if (!accountRepository.accountExists(accountId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                accountRepository.getTransactions(accountId)
        );
    }
}
