package com.example.deal.mappers;

import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.entity.Credit;

public class CreditMapper {
    public Credit mapToEntity(LoanOfferDTO request) {
        return Credit.builder()
                .insuranceEnable(request.getIsInsuranceEnabled())
                .salaryClient(request.getIsSalaryClient())
                .build();
    }
}
