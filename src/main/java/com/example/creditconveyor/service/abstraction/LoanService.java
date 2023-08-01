package com.example.creditconveyor.service.abstraction;

import com.example.creditconveyor.dto.CreditDTO;
import com.example.creditconveyor.dto.LoanApplicationRequestDTO;
import com.example.creditconveyor.dto.LoanOfferDTO;
import com.example.creditconveyor.dto.ScoringDataDTO;
import com.example.creditconveyor.exception.IllegalLoanRequestException;

import java.util.List;

public interface LoanService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);

    CreditDTO calculateCredit(ScoringDataDTO scoringDataDTO) throws IllegalLoanRequestException;
}
