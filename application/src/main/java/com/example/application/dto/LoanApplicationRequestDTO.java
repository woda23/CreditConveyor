package com.example.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequestDTO {
    public BigDecimal amount;
    public Integer term;
    public String firstName;
    public String lastName;
    public String middleName;
    public String email;
    public LocalDate birthdate;
    public String passportSeries;
    public String passportNumber;
}
