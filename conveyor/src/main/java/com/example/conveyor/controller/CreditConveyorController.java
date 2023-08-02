package com.example.conveyor.controller;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.service.abstraction.LoanService;
import com.example.conveyor.wrapper.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
public class CreditConveyorController {

    private final LoanService loanService;

    private static final Logger log = LoggerFactory.getLogger(CreditConveyorController.class);

    @Operation(summary = "Get loan offers",
            description = "Calculates possible loan offers based on the data of the loan application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offers returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan application data")})
    @PostMapping("/offers")

    public ResponseEntity<Object> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        try {
            log.info("Loan application - amount: {}, term: {}, firstName: {}, lastName: {}, middleName: {}, email: {}," +
                            " birthdate: {}, passportSeries: {}, passportNumber: {}",
                    loanApplicationRequestDTO.getAmount(), loanApplicationRequestDTO.getTerm(),
                    loanApplicationRequestDTO.getFirstName(), loanApplicationRequestDTO.getLastName(),
                    loanApplicationRequestDTO.getMiddleName(), loanApplicationRequestDTO.getEmail(),
                    loanApplicationRequestDTO.getBirthdate(), loanApplicationRequestDTO.getPassportSeries(),
                    loanApplicationRequestDTO.getPassportNumber());
            return new ResponseEntity<>(loanService.getLoanOffers(loanApplicationRequestDTO), HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get credit offer",
            description = "Calculates possible credit offer by ScoringData")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit offer returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credit application data")})
    @PostMapping("/calculation")
    public ResponseEntity<Object> calculateCredit(@RequestBody ScoringDataDTO scoringData) {
        try {
            log.info("Scoring data - amount: {}, term: {}, firstName: {}, lastName: {}, middleName: {}, gender: {}," +
                            " birthdate: {}, passportSeries: {}, passportNumber: {}, passportIssueDate: {}," +
                            " passportIssueBranch: {}, maritalStatus: {}, dependentAmount: {}, employment: {}," +
                            " account: {}, isInsuranceEnabled: {}, isSalaryClient: {}",
                    scoringData.getAmount(), scoringData.getTerm(), scoringData.getFirstName(),
                    scoringData.getLastName(), scoringData.getMiddleName(), scoringData.getGender(),
                    scoringData.getBirthdate(),scoringData.getPassportSeries(), scoringData.getPassportNumber(),
                    scoringData.getPassportIssueDate(),scoringData.getPassportIssueBranch(),
                    scoringData.getMaritalStatus(), scoringData.getDependentAmount(), scoringData.getEmployment(),
                    scoringData.getAccount(), scoringData.getIsInsuranceEnabled(), scoringData.getIsSalaryClient());
            return new ResponseEntity<>(loanService.calculateCredit(scoringData), HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
