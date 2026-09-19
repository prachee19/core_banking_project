package com.dbms.core_banking.dto;

import java.math.BigDecimal;

public class LoanPaymentRequest {
    private BigDecimal amountPaid;

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
}
