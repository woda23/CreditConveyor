package com.example.deal.controller;

import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.service.realization.DealServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final DealServiceImpl dealServiceImpl;

    @Operation(summary = "Get loan offers",
            description = "Calculates possible loan offers based on the data of the loan application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offers returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan application data")})
    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDTO>> calculateLoanOffers(@RequestBody LoanApplicationRequestDTO request) {
        log.info("calculateLoanOffers(), LoanApplicationRequestDTO: {}", request);
        List<LoanOfferDTO> loanOffers = dealServiceImpl.getLoanOffers(request);
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
        dealServiceImpl.selectLoanOffer(offer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Calculate CreditDTO with FinishRegistrationRequestDTO",
            description = "Calculate CreditDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid FinishRegistrationRequestDTO")})
    @PutMapping("/calculate/{applicationId}")
    public ResponseEntity<Void> calculateLoan(@RequestBody FinishRegistrationRequestDTO request,
                                                   @PathVariable("applicationId") Long applicationId) {
        log.info("calculateLoan(), FinishRegistrationRequestDTO: {}, Long: {}", request, applicationId);
        var creditDTO = dealServiceImpl.calculateLoan(request, applicationId);
        log.info("calculateLoan(), CreditDTO: {}", creditDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
