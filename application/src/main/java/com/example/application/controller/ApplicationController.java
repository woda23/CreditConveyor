package com.example.application.controller;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.service.realization.ApplicationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationServiceImpl applicationService;

    @Operation(summary = "Get loan offers",
            description = "Calculates possible loan offers based on the data of the loan application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offers returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan application data")})
    @PostMapping("/")
    public ResponseEntity<List<LoanOfferDTO>> calculateLoanOffers(@RequestBody LoanApplicationRequestDTO request) {
        log.info("calculateLoanOffers(), LoanApplicationRequestDTO: {}", request);
        List<LoanOfferDTO> loanOffers = applicationService.getLoanOffers(request);
        log.info("calculateLoanOffers(), List<LoanOfferDTO>: {}", loanOffers);
        return new ResponseEntity<>(loanOffers, HttpStatus.OK);
    }

    @Operation(summary = "Select loan offer",
            description = "Put to the DB selected loan offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid LoanOfferDTO")})
    @PutMapping("/offer")
    public ResponseEntity<Void> selectLoanOffer(@RequestBody LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        applicationService.selectLoanOffer(offer);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
