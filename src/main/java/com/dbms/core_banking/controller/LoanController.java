package com.dbms.core_banking.controller;

import com.dbms.core_banking.dto.LoanPaymentRequest;
import com.dbms.core_banking.dto.LoanRequest;
import com.dbms.core_banking.repository.UserRepository;
import com.dbms.core_banking.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    public LoanController(
            LoanService loanService,
            UserRepository userRepository) {

        this.loanService = loanService;
        this.userRepository = userRepository;
    }

    // CUSTOMER: Apply for a loan
    @PostMapping("/loans/apply")
    public ResponseEntity<String> applyForLoan(
            @RequestBody LoanRequest request,
            Authentication authentication) {

        try {
            Integer customerId =
                    userRepository.getCustomerIdByUsername(
                            authentication.getName()
                    );

            if (customerId == null) {
                return ResponseEntity.badRequest()
                        .body("Customer account is not linked to a customer");
            }

            request.setCustomerId(customerId);

            loanService.applyForLoan(request);

            return ResponseEntity.ok(
                    "Loan application submitted successfully"
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // CUSTOMER: View only their own loans
    @GetMapping("/loans/my")
    public ResponseEntity<?> getMyLoans(
            Authentication authentication) {

        Integer customerId =
                userRepository.getCustomerIdByUsername(
                        authentication.getName()
                );

        if (customerId == null) {
            return ResponseEntity.badRequest()
                    .body("Customer account is not linked to a customer");
        }

        return ResponseEntity.ok(
                loanService.getCustomerLoans(customerId)
        );
    }

    // ADMIN: View pending loan applications
    @GetMapping("/admin/loans/pending")
    public ResponseEntity<?> getPendingLoans() {
        return ResponseEntity.ok(
                loanService.getPendingLoans()
        );
    }

    // ADMIN: Approve loan
    @PutMapping("/admin/loans/{loanId}/approve")
    public ResponseEntity<String> approveLoan(
            @PathVariable Integer loanId) {

        try {
            loanService.approveLoan(loanId);

            return ResponseEntity.ok(
                    "Loan approved successfully"
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ADMIN: Reject loan
    @PutMapping("/admin/loans/{loanId}/reject")
    public ResponseEntity<String> rejectLoan(
            @PathVariable Integer loanId) {

        try {
            loanService.rejectLoan(loanId);

            return ResponseEntity.ok(
                    "Loan rejected successfully"
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // CUSTOMER: Make payment only on their own loan
    @PostMapping("/loans/{loanId}/payments")
    public ResponseEntity<String> makePayment(
            @PathVariable Integer loanId,
            @RequestBody LoanPaymentRequest request,
            Authentication authentication) {

        try {
            Integer customerId =
                    userRepository.getCustomerIdByUsername(
                            authentication.getName()
                    );

            if (customerId == null) {
                return ResponseEntity.badRequest()
                        .body("Customer account is not linked to a customer");
            }

            loanService.makePayment(
                    loanId,
                    customerId,
                    request
            );

            return ResponseEntity.ok(
                    "Loan payment successful"
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // CUSTOMER: View payments for their own loan
    @GetMapping("/loans/{loanId}/payments")
    public ResponseEntity<?> getPayments(
            @PathVariable Integer loanId,
            Authentication authentication) {

        try {
            Integer customerId =
                    userRepository.getCustomerIdByUsername(
                            authentication.getName()
                    );

            Integer loanCustomerId =
                    loanService.getCustomerId(loanId);

            if (!customerId.equals(loanCustomerId)) {
                return ResponseEntity.status(403)
                        .body("You are not authorized to view this loan");
            }

            return ResponseEntity.ok(
                    loanService.getPayments(loanId)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
