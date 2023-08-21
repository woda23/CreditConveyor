package com.example.deal.service.abstraction;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO request);
    void selectLoanOffer(LoanOfferDTO offer);
    CreditDTO calculateLoan(FinishRegistrationRequestDTO request, Long applicationId);

}
