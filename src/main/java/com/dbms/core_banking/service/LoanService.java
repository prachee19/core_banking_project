package com.dbms.core_banking.service;

import com.dbms.core_banking.dto.LoanPaymentRequest;
import com.dbms.core_banking.dto.LoanRequest;
import com.dbms.core_banking.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    public void applyForLoan(LoanRequest request) {

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        if (request.getLoanAmount() == null ||
                request.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Loan amount must be greater than zero");
        }

        if (request.getInterestRate() == null ||
                request.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Interest rate cannot be negative");
        }

        if (request.getStartDate() == null ||
                request.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        loanRepository.createLoan(
                request.getCustomerId(),
                request.getLoanAmount(),
                request.getInterestRate(),
                Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()));
    }

    public List<Map<String, Object>> getCustomerLoans(Integer customerId) {
        return loanRepository.getLoansByCustomer(customerId);
    }

    public List<Map<String, Object>> getPendingLoans() {
        return loanRepository.getPendingLoans();
    }

    public Integer getCustomerId(Integer loanId) {
        validateLoan(loanId);
        return loanRepository.getCustomerId(loanId);
    }

    @Transactional
    public void approveLoan(Integer loanId) {

        validateLoan(loanId);

        Map<String, Object> loan = loanRepository.getLoan(loanId);
        String status = String.valueOf(loan.get("status"));

        if (!"PENDING".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException(
                    "Only pending loans can be approved");
        }

        loanRepository.updateLoanStatus(loanId, "APPROVED");
    }

    @Transactional
    public void rejectLoan(Integer loanId) {

        validateLoan(loanId);

        Map<String, Object> loan = loanRepository.getLoan(loanId);
        String status = String.valueOf(loan.get("status"));

        if (!"PENDING".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException(
                    "Only pending loans can be rejected");
        }

        loanRepository.updateLoanStatus(loanId, "REJECTED");
    }

    @Transactional
    public void makePayment(
            Integer loanId,
            Integer customerId,
            LoanPaymentRequest request) {

        validateLoan(loanId);

        Integer loanCustomerId = loanRepository.getCustomerId(loanId);

        if (!customerId.equals(loanCustomerId)) {
            throw new IllegalArgumentException(
                    "You are not authorized to pay this loan");
        }

        Map<String, Object> loan = loanRepository.getLoan(loanId);
        String status = String.valueOf(loan.get("status"));

        if (!"APPROVED".equalsIgnoreCase(status) &&
                !"ACTIVE".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException(
                    "Loan is not available for payment");
        }

        if (request == null ||
                request.getAmountPaid() == null ||
                request.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero");
        }

        BigDecimal loanAmount = loanRepository.getLoanAmount(loanId);
        BigDecimal totalPaid = loanRepository.getTotalPaid(loanId);

        BigDecimal remaining = loanAmount.subtract(totalPaid);

        if (request.getAmountPaid().compareTo(remaining) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds remaining loan amount");
        }

        loanRepository.addPayment(
                loanId,
                request.getAmountPaid());

        BigDecimal newTotalPaid = totalPaid.add(request.getAmountPaid());

        if (newTotalPaid.compareTo(loanAmount) >= 0) {
            loanRepository.updateLoanStatus(loanId, "PAID");
        } else {
            loanRepository.updateLoanStatus(loanId, "ACTIVE");
        }
    }

    public List<Map<String, Object>> getPayments(Integer loanId) {

        validateLoan(loanId);

        return loanRepository.getPayments(loanId);
    }

    private void validateLoan(Integer loanId) {

        if (loanId == null || !loanRepository.loanExists(loanId)) {
            throw new IllegalArgumentException("Loan not found");
        }
    }
}