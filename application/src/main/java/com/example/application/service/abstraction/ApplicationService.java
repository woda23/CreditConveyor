package com.example.application.service.abstraction;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;

import java.util.List;

public interface ApplicationService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO request);
    void selectLoanOffer(LoanOfferDTO offer);
}
