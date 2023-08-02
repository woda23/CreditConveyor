package com.example.conveyor.service.abstraction;

import com.example.conveyor.dto.LoanApplicationRequestDTO;


public interface LoanPreScoringService {
    void preScoreLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
