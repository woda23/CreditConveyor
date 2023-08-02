package com.example.conveyor.controller;

import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.service.abstraction.LoanService;
import com.example.conveyor.wrapper.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
@Slf4j
public class CreditConveyorController {

    private final LoanService loanService;

    @Operation(summary = "Get loan offers",
            description = "Calculates possible loan offers based on the data of the loan application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offers returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan application data")})
    @PostMapping("/offers")

    public ResponseEntity<Object> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
            log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", loanApplicationRequestDTO);
            List<LoanOfferDTO> loanOffers = loanService.getLoanOffers(loanApplicationRequestDTO);
            log.info("getLoanOffers(), List<LoanOfferDTO>: {}", loanOffers);
            return new ResponseEntity<>(loanOffers, HttpStatus.OK);
    }

    @Operation(summary = "Get credit offer",
            description = "Calculates possible credit offer by ScoringData")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit offer returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credit application data")})
    @PostMapping("/calculation")
    public ResponseEntity<Object> calculateCredit(@RequestBody ScoringDataDTO scoringData) {
            log.info("calculateCredit(), ScoringDataDTO: {}", scoringData);
            CreditDTO creditDTO = loanService.calculateCredit(scoringData);
            log.info("calculateCredit(), CreditDTO: {}", scoringData);
            return new ResponseEntity<>(creditDTO, HttpStatus.OK);
    }
}
