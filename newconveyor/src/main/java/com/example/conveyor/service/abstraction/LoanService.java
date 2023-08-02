package com.example.conveyor.service.abstraction;

import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.exception.IllegalLoanRequestException;

import java.util.List;

public interface LoanService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);

    CreditDTO calculateCredit(ScoringDataDTO scoringDataDTO) throws IllegalLoanRequestException;
}
