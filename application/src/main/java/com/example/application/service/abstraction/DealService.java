package com.example.application.service.abstraction;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    void selectLoanOffer(LoanOfferDTO offer);
}
