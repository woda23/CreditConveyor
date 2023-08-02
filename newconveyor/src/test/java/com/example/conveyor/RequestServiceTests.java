package com.example.conveyor;

import com.example.conveyor.controller.CreditConveyorController;
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
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        Assertions.assertEquals(loanOffers.size(), 4);
        Assertions.assertEquals(loanOffers.get(3).getRate(), BigDecimal.valueOf(13));
        Assertions.assertEquals(loanOffers.get(3).getTotalAmount(), BigDecimal.valueOf(150000.0));
        Assertions.assertEquals(loanOffers.get(2).getMonthlyPayment(), BigDecimal.valueOf(13581.25));
        assertTrue(loanOffers.get(0).getRate().compareTo(loanOffers.get(1).getRate()) >= 0);
        assertTrue(loanOffers.get(1).getRate().compareTo(loanOffers.get(2).getRate()) >= 0);
        assertTrue(loanOffers.get(2).getRate().compareTo(loanOffers.get(3).getRate()) >= 0);
    }

    @Test
    void testLoanMethodForFailed() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(50000.00));
        loanApplicationRequestDTO.setBirthdate(LocalDate.parse("1990-01-01"));
        loanApplicationRequestDTO.setEmail("ivanov@mail.ru");
        loanApplicationRequestDTO.setTerm(12);
        loanApplicationRequestDTO.setLastName("Ivanov");
        loanApplicationRequestDTO.setFirstName("Иван");
        loanApplicationRequestDTO.setMiddleName("Ivanovich");
        loanApplicationRequestDTO.setPassportSeries("12321314");
        loanApplicationRequestDTO.setPassportNumber("1234");

        assertThrows(IllegalArgumentException.class, () -> loanPreScoringService.preScoreLoanApplication(loanApplicationRequestDTO));
        loanApplicationRequestDTO.setFirstName("Ivan");
        loanApplicationRequestDTO.setPassportNumber("1234242");
        assertThrows(IllegalArgumentException.class, () -> loanPreScoringService.preScoreLoanApplication(loanApplicationRequestDTO));
        loanApplicationRequestDTO.setPassportNumber("1234");
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
        Assertions.assertEquals(calculateCredit.getAmount(), BigDecimal.valueOf(150000.0));
        Assertions.assertEquals(calculateCredit.getPsk(), BigDecimal.valueOf(227.996));
        Assertions.assertEquals(calculateCredit.getMonthlyPayment(), BigDecimal.valueOf(13666.48));
        Assertions.assertEquals(calculateCredit.getPaymentSchedule().size(), 12);
        assertTrue(calculateCredit.getPaymentSchedule().get(11).getRemainingDebt()
                .compareTo(BigDecimal.valueOf(0)) >= 0);
    }

    @Test
    void testCreditMethodForFailed() {
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
                        EmploymentStatus.UNEMPLOYED,
                        "2122",
                        BigDecimal.valueOf(5000),
                        Position.TOP_MANAGER,
                        60,
                        12
                ));
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.setIsSalaryClient(true);

        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
        scoringDataDTO.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        scoringDataDTO.getEmployment().setSalary(BigDecimal.valueOf(12));
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
        scoringDataDTO.getEmployment().setSalary(BigDecimal.valueOf(10000));
        scoringDataDTO.setBirthdate(LocalDate.parse("2022-01-01"));
        assertThrows(IllegalLoanRequestException.class, () -> service.calculateCredit(scoringDataDTO));
    }
}
