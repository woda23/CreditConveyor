package com.example.deal.mappers;

import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.entity.Passport;
import com.example.deal.entity.jsonb.PassportData;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PassportMapper {
    public Passport mapToEntity(LoanApplicationRequestDTO request) {
        return new Passport(
                new PassportData(request.getPassportSeries(), request.getPassportNumber(),null, null));
    }
}
