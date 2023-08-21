package com.example.deal.mappers;

import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;

@NoArgsConstructor
@Component
public class ApplicationMapper {
    public Application mapToApplication(Client client) {
        return Application.builder()
                .client(client)
                .creationData(Timestamp.valueOf(LocalDate.now().atStartOfDay()))
                .build();
    }
}
