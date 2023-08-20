package com.example.deal.service.abstraction;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;

import java.util.List;

public interface LoanService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO);
}
