package com.example.deal.mappers;

import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@NoArgsConstructor
public class ClientMapper {
    public Client mapToEntity(LoanApplicationRequestDTO request) {
        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .birthDate(request.getBirthdate())
                .build();
    }

    public Application entityToMap(Client client) {
        return Application.builder()
                .client(client)
                .creationData(Timestamp.valueOf(LocalDate.now().atStartOfDay()))
                .build();
    }
}
