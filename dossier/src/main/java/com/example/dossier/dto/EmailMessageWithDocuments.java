package com.example.dossier.dto;

import com.example.dossier.dto.enums.EmailMessageTheme;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessageWithDocuments {
    private String address;
    private EmailMessageTheme theme;
    private String credit;
    private Long applicationId;
}
