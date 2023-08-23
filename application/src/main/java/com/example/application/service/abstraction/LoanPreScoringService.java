package com.example.application.service.abstraction;


import com.example.application.dto.LoanApplicationRequestDTO;

public interface LoanPreScoringService {
    void preScoreLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
