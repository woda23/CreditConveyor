package com.example.conveyor;

import com.example.conveyor.controller.CreditConveyorController;
import com.example.conveyor.controller.ExceptionHandlingController;
import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.EmploymentDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.dto.enums.EmploymentStatus;
import com.example.conveyor.dto.enums.Gender;
import com.example.conveyor.dto.enums.MaritalStatus;
import com.example.conveyor.dto.enums.Position;
import com.example.conveyor.exception.IllegalLoanRequestException;
import com.example.conveyor.service.abstraction.LoanPreScoringService;
import com.example.conveyor.service.realization.LoanServiceImpl;
import com.example.conveyor.wrapper.ErrorResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class RequestServiceTests {

    @Autowired
    CreditConveyorController controller;

    @Autowired
    LoanServiceImpl service;

    @Autowired
    LoanPreScoringService loanPreScoringService;

    @Autowired
    ExceptionHandlingController exceptionHandlingController;

    @Test
    void testLoanMethodForPass() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(50000.00));
        loanApplicationRequestDTO.setBirthdate(LocalDate.parse("1990-01-01"));
        loanApplicationRequestDTO.setEmail("ivanov@mail.ru");
        loanApplicationRequestDTO.setTerm(12);
        loanApplicationRequestDTO.setLastName("Ivanov");
        loanApplicationRequestDTO.setFirstName("Ivan");
        loanApplicationRequestDTO.setMiddleName("Ivanovich");
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setPassportNumber("123456");

        List<LoanOfferDTO> loanOffers = (List<LoanOfferDTO>) controller.getLoanOffers(loanApplicationRequestDTO).getBody();
        assertEquals(loanOffers.size(), 4);
        assertEquals(loanOffers.get(3).getRate(), BigDecimal.valueOf(13));
        assertEquals(loanOffers.get(3).getTotalAmount(), BigDecimal.valueOf(150000.0));
        assertEquals(loanOffers.get(2).getMonthlyPayment(), BigDecimal.valueOf(13581.25));
        assertTrue(loanOffers.get(0).getRate().compareTo(loanOffers.get(1).getRate()) >= 0);
        assertTrue(loanOffers.get(1).getRate().compareTo(loanOffers.get(2).getRate()) >= 0);
        assertTrue(loanOffers.get(2).getRate().compareTo(loanOffers.get(3).getRate()) >= 0);

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

    @Test
    void testCreditMethodServiceForPass() {
        var scoringDataDTO = new ScoringDataDTO();
        scoringDataDTO.setAmount(BigDecimal.valueOf(50000.00));
        scoringDataDTO.setBirthdate(LocalDate.parse("1990-01-01"));
        scoringDataDTO.setAccount("1234567890");
        scoringDataDTO.setTerm(12);
        scoringDataDTO.setLastName("Ivanov");
        scoringDataDTO.setFirstName("Ivan");
        scoringDataDTO.setMiddleName("Ivanovich");
        scoringDataDTO.setPassportSeries("1234");
        scoringDataDTO.setPassportNumber("123456");
        scoringDataDTO.setGender(Gender.MALE);
        scoringDataDTO.setPassportIssueBranch("Отделением УФМС по г. Москве");
        scoringDataDTO.setPassportIssueDate(LocalDate.parse("2010-01-01"));
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);
        scoringDataDTO.setDependentAmount(2);
        scoringDataDTO.setEmployment(
                new EmploymentDTO(
                        EmploymentStatus.SELF_EMPLOYED,
                        "2122",
                        BigDecimal.valueOf(5000),
                        Position.TOP_MANAGER,
                        60,
                        12
                ));
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.setIsSalaryClient(true);


        var calculateCredit = (CreditDTO) controller.calculateCredit(scoringDataDTO).getBody();
        assertEquals(calculateCredit.getAmount(), BigDecimal.valueOf(150000.0));
        assertEquals(calculateCredit.getPsk(), BigDecimal.valueOf(227.996));
        assertEquals(calculateCredit.getMonthlyPayment(), BigDecimal.valueOf(13666.48));
        assertEquals(calculateCredit.getPaymentSchedule().size(), 12);
        assertTrue(calculateCredit.getPaymentSchedule().get(11).getRemainingDebt()
                .compareTo(BigDecimal.valueOf(0)) >= 0);
    }

    @Test
    void testCreditMethodForFailed() {
        var scoringDataDTO = new ScoringDataDTO();
        scoringDataDTO.setAmount(BigDecimal.valueOf(50000.00));
        scoringDataDTO.setBirthdate(LocalDate.parse("1980-01-01"));
        scoringDataDTO.setAccount("1234567890");
        scoringDataDTO.setTerm(12);
        scoringDataDTO.setLastName("Ivanov");
        scoringDataDTO.setFirstName("Ivan");
        scoringDataDTO.setMiddleName("Ivanovich");
        scoringDataDTO.setPassportSeries("1234");
        scoringDataDTO.setPassportNumber("123456");
        scoringDataDTO.setGender(Gender.FEMALE);
        scoringDataDTO.setPassportIssueBranch("Отделением УФМС по г. Москве");
        scoringDataDTO.setPassportIssueDate(LocalDate.parse("2010-01-01"));
        scoringDataDTO.setMaritalStatus(MaritalStatus.DIVORCED);
        scoringDataDTO.setDependentAmount(2);
        scoringDataDTO.setEmployment(
                new EmploymentDTO(
                        EmploymentStatus.UNEMPLOYED,
                        "2122",
                        BigDecimal.valueOf(5000),
                        Position.MID_MANAGER,
                        60,
                        12
                ));
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.setIsSalaryClient(true);
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
        scoringDataDTO.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        scoringDataDTO.getEmployment().setWorkExperienceTotal(11);
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
        scoringDataDTO.setGender(Gender.NON_BINARY);
        scoringDataDTO.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
        scoringDataDTO.getEmployment().setSalary(BigDecimal.valueOf(20));
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
        scoringDataDTO.getEmployment().setSalary(BigDecimal.valueOf(10000));
        scoringDataDTO.setBirthdate(LocalDate.parse("2022-01-01"));
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
    }

    @Test
    void checkCorrectExceptionHandler() {
        NullPointerException exception = new NullPointerException("Null pointer exception occurred");
        ResponseEntity<ErrorResponse> response = exceptionHandlingController.handleInvalidLoanApplicationDataException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
