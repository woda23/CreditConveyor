package com.example.dossier.dto;

import com.example.dossier.dto.enums.EmailMessageTheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailMessage {
    private String address;
    private EmailMessageTheme theme;
    private Long applicationId;
}
