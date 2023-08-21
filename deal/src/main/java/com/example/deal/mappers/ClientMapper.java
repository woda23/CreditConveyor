package com.example.deal.mappers;

import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.entity.Client;
import com.example.deal.entity.Employment;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class ClientMapper {
    public Client mapToEntity(LoanApplicationRequestDTO request) {
        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .birthDate(request.getBirthdate())
                .employment(new Employment())
                .build();
    }
}
