package com.example.deal.entity;

import com.example.deal.dto.PaymentScheduleElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentScheduleElementEntity {
    private Integer number;
    private String date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;
    private BigDecimal debtPayment;
    private BigDecimal remainingDebt;
    public PaymentScheduleElementEntity(PaymentScheduleElement paymentScheduleElement) {
        this.number = paymentScheduleElement.getNumber();
        this.date = paymentScheduleElement.getDate();
        this.totalPayment = paymentScheduleElement.getTotalPayment();
        this.interestPayment = paymentScheduleElement.getInterestPayment();
        this.debtPayment = paymentScheduleElement.getDebtPayment();
        this.remainingDebt = paymentScheduleElement.getRemainingDebt();
    }
}
