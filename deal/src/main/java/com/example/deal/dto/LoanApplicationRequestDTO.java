package com.example.deal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoanApplicationRequestDTO {
    public BigDecimal amount;
    public Integer term;
    public String firstName;
    public String lastName;
    public String middleName;
    public String email;
    public String birthdate;
    public String passportSeries;
    public String passportNumber;
}
