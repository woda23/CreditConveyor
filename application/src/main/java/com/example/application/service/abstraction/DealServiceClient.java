package com.example.application.service.abstraction;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

public interface DealServiceClient {
    @Headers("Content-Type: application/json")
    @RequestLine("POST /deal/application")
    List<LoanOfferDTO> sendLoanApplication(LoanApplicationRequestDTO request);

    @Headers("Content-Type: application/json")
    @RequestLine("PUT /deal/offer")
    List<LoanOfferDTO> sendLoanOffer(LoanOfferDTO request);
}
