package com.example.deal.dto;

import com.example.deal.dto.enums.EmailMessageTheme;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {
    private String address;
    private EmailMessageTheme theme;
    private Long applicationId;
}
