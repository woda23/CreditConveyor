package com.example.deal.dto;

import com.example.deal.dto.enums.Gender;
import com.example.deal.dto.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishRegistrationRequestDTO {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private String passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDTO employment;
    private String account;
}
