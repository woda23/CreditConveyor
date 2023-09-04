package com.example.application.service.realization;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.service.abstraction.ApplicationService;
import com.example.application.service.abstraction.DealService;
import com.example.application.service.abstraction.LoanPreScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final DealService dealServiceImpl;
    private final LoanPreScoringService loanPreScoringService;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO request) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", request);
        loanPreScoringService.preScoreLoanApplication(request);
        List<LoanOfferDTO> loanOffers = dealServiceImpl.getLoanOffers(request);
        log.info("getLoanOffers(), List<LoanOfferDTO>: {}", loanOffers);
        return loanOffers;
    }

    public void selectLoanOffer(LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        dealServiceImpl.selectLoanOffer(offer);
    }
}
