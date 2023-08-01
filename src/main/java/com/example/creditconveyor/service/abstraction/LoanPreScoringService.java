package com.example.creditconveyor.service.abstraction;

import com.example.creditconveyor.dto.LoanApplicationRequestDTO;


public interface LoanPreScoringService {
    void preScoreLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
