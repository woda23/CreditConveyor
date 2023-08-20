package com.example.deal.mappers;

import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.entity.Credit;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class CreditMapper {
    public Credit mapToEntity(LoanOfferDTO request) {
        return Credit.builder()
                .insuranceEnable(request.getIsInsuranceEnabled())
                .salaryClient(request.getIsSalaryClient())
                .build();
    }
}
