package com.example.application.service.realization;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.service.abstraction.DealService;
import com.example.application.service.abstraction.DealServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    @Value("${deal.host.url}")
    private String hostValue;
    private final DealServiceClient dealServiceClient;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        try {
            List<LoanOfferDTO> response = dealServiceClient.sendLoanApplication(loanApplicationRequestDTO);
            log.info("getLoanOffers(), List<LoanOfferDTO>: {}", response);
            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void selectLoanOffer(LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        try {
            dealServiceClient.sendLoanOffer(offer);
            log.info("selectLoanOffer(), request has been sent: {}", offer);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
