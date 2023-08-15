package com.example.deal.controller;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Slf4j
public class DealController {

    private final DealService dealService;

    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDTO>> calculateLoanOffers(@RequestBody LoanApplicationRequestDTO request) {
        List<LoanOfferDTO> loanOffers = dealService.getLoanOffers(request);
        return new ResponseEntity<>(loanOffers, HttpStatus.OK);
    }

    @PutMapping("/offer")
    public ResponseEntity<Void> selectLoanOffer(@RequestBody LoanOfferDTO offer) {
        dealService.selectLoanOffer(offer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/calculate/{applicationId}")
    public ResponseEntity<CreditDTO> calculateLoan(@RequestBody FinishRegistrationRequestDTO request,
                                                   @PathVariable("applicationId") Long applicationId) {
        var creditDTO = dealService.calculateLoan(request, applicationId);
        return new ResponseEntity<>(creditDTO, HttpStatus.OK);
    }
}
