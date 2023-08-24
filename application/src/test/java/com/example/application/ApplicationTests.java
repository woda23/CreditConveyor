package com.example.application;

import com.example.application.controller.ApplicationController;
import com.example.application.controller.ExceptionHandlingController;
import com.example.application.dto.ErrorResponse;
import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.service.abstraction.LoanPreScoringService;
import com.example.application.service.realization.DealServiceImpl;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ApplicationTests {
    @Autowired
    LoanPreScoringService loanPreScoringService;

    @Autowired
    DealServiceImpl dealServiceImpl;

    @Autowired
    ApplicationController applicationController;

    @Autowired
    ExceptionHandlingController exceptionHandlingController;
    @Test
    public void checkCorrectGetLoanOffers() {
        LoanApplicationRequestDTO request = getLoanApplicationRequestDTO();

        List<LoanOfferDTO> response = new ArrayList<>();
        response.add(new LoanOfferDTO(1L, BigDecimal.valueOf(500000.00),
                BigDecimal.valueOf(500000.00), 12, BigDecimal.valueOf(46989.91),  BigDecimal.valueOf(23),
                false, false));
        response.add(new LoanOfferDTO(1L, BigDecimal.valueOf(500000.00),
                BigDecimal.valueOf(500000.00), 12, BigDecimal.valueOf(46989.91),  BigDecimal.valueOf(23),
                false, true));
        response.add(new LoanOfferDTO(1L, BigDecimal.valueOf(500000.00),
                BigDecimal.valueOf(500000.00), 12, BigDecimal.valueOf(46989.91),  BigDecimal.valueOf(23),
                true, false));
        response.add(new LoanOfferDTO(1L, BigDecimal.valueOf(500000.00),
                BigDecimal.valueOf(500000.00), 12, BigDecimal.valueOf(46989.91),  BigDecimal.valueOf(23),
                true, true));

        DealServiceImpl dealService = Mockito.mock(DealServiceImpl.class);
        when(dealService.getLoanOffers(request)).thenReturn(response);
        List<LoanOfferDTO> loanOffers = dealService.getLoanOffers(request);
        var feign = dealServiceImpl.configureFeign();
        Assertions.assertEquals(loanOffers.size(), 4);
        LoanOfferDTO loanOfferDTO = loanOffers.get(0);
        Assertions.assertEquals(loanOfferDTO.getApplicationId(), 1L);
        Assertions.assertEquals(loanOfferDTO.getRequestedAmount(), BigDecimal.valueOf(500000.00));
        Assertions.assertEquals(loanOfferDTO.getTotalAmount(), BigDecimal.valueOf(500000.00));
        Assertions.assertEquals(loanOfferDTO.getTerm(), 12);
        Assertions.assertEquals(loanOfferDTO.getMonthlyPayment(), BigDecimal.valueOf(46989.91));
        Assertions.assertEquals(loanOfferDTO.getRate(), BigDecimal.valueOf(23));
        Assertions.assertEquals(loanOfferDTO.getIsInsuranceEnabled(), false);
        Assertions.assertEquals(loanOfferDTO.getIsSalaryClient(), false);
        Assertions.assertNotNull(feign);

        try {
            applicationController.calculateLoanOffers(request);
        }
        catch (IllegalArgumentException e) {
            ResponseEntity<ErrorResponse> responseEntity
                    = exceptionHandlingController.handleInvalidLoanApplicationDataException(e);
            Assertions.assertNotNull(responseEntity);
            Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(400));
        }

        var client = dealService.getLoanOffers(request);
        Assertions.assertNotNull(client);
    }

    @Test
    public void checkCorrectSelectLoanOffer() {
        LoanOfferDTO loanOfferDTO = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(500000.00))
                .totalAmount(BigDecimal.valueOf(500000.00))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(46989.91))
                .rate(BigDecimal.valueOf(23))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
        DealServiceImpl dealService = Mockito.mock(DealServiceImpl.class);
        doNothing().when(dealService).selectLoanOffer(loanOfferDTO);
        dealService.selectLoanOffer(loanOfferDTO);
        try {
            applicationController.selectLoanOffer(loanOfferDTO);
        }
        catch (IllegalArgumentException e) {
            ResponseEntity<ErrorResponse> responseEntity
                    = exceptionHandlingController.handleInvalidLoanApplicationDataException(e);
            Assertions.assertNotNull(responseEntity);
            Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(400));
        }
    }

    @Test
    void testLoanMethodForFailed() {
        assertThrows(NullPointerException.class, () -> loanPreScoringService.preScoreLoanApplication(null));
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        loanApplicationRequestDTO.setAmount(null);
        loanApplicationRequestDTO.setBirthdate(LocalDate.parse("2010-01-01"));
        loanApplicationRequestDTO.setEmail("ivanov@mail.ru");
        loanApplicationRequestDTO.setTerm(5);
        loanApplicationRequestDTO.setLastName("Иванов");
        loanApplicationRequestDTO.setFirstName("Иван");
        loanApplicationRequestDTO.setMiddleName("Иваноич");
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setPassportNumber("123456");

        assertThrows(IllegalArgumentException.class, () -> loanPreScoringService.preScoreLoanApplication(loanApplicationRequestDTO));
        loanApplicationRequestDTO.setFirstName("Ivan");
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(50000.00));
        loanApplicationRequestDTO.setTerm(6);
        loanApplicationRequestDTO.setBirthdate(LocalDate.parse("1990-01-01"));
        loanApplicationRequestDTO.setPassportNumber("1234242");
        assertThrows(IllegalArgumentException.class, () -> loanPreScoringService.preScoreLoanApplication(loanApplicationRequestDTO));
        loanApplicationRequestDTO.setPassportSeries("11111");
        loanApplicationRequestDTO.setEmail("ivanov.ru");
        assertThrows(IllegalArgumentException.class, () -> loanPreScoringService.preScoreLoanApplication(loanApplicationRequestDTO));
    }

    private static LoanApplicationRequestDTO getLoanApplicationRequestDTO() {
        return LoanApplicationRequestDTO.builder()
                .amount(BigDecimal.valueOf(500000.00))
                .term(12)
                .firstName("IvanIvanIvan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivanov@mail.ru")
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportNumber("123456")
                .passportSeries("1234")
                .build();
    }

}
