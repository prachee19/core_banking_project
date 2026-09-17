package com.dbms.core_banking.controller;

import com.dbms.core_banking.dto.TransferRequest;
import com.dbms.core_banking.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {

        try {
            String referenceNumber = transferService.transfer(
                    request.getSenderAccountId(),
                    request.getReceiverAccountId(),
                    request.getAmount()
            );

            return ResponseEntity.ok(
                    "Transfer successful. Reference: " + referenceNumber
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
