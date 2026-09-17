package com.dbms.core_banking.controller;

import com.dbms.core_banking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable Integer accountId,
            @RequestBody Map<String, BigDecimal> request) {

        try {
            accountService.deposit(accountId, request.get("amount"));
            return ResponseEntity.ok("Deposit successful");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable Integer accountId,
            @RequestBody Map<String, BigDecimal> request) {

        try {
            accountService.withdraw(accountId, request.get("amount"));
            return ResponseEntity.ok("Withdrawal successful");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
