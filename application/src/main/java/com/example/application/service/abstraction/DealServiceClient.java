package com.example.application.service.abstraction;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "deal-service-client", url = "http://localhost:8090")
public interface DealServiceClient {
    @PostMapping("${deal.url.application}")
    List<LoanOfferDTO> sendLoanApplication(LoanApplicationRequestDTO request);

    @PutMapping("${deal.url.offer}")
    List<LoanOfferDTO> sendLoanOffer(LoanOfferDTO request);
}
