package com.example.deal;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.DealService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DealServiceTests {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DealService dealService;

    @Test
    public void checkCorrectGetLoanOffers() {
        String json = "{\"amount\": 500000.00,\"term\": 12,\"firstName\": \"IvanIvanIvan\",\"lastName\": \"Ivanov\",\"middleName\": \"Ivanovich\",\"email\": \"ivanov@mail.ru\",\"birthdate\":\"1990-01-01\",\"passportSeries\": \"1234\",\"passportNumber\": \"123456\"}";
        LoanApplicationRequestDTO loanApplicationRequestDTO = new Gson().fromJson(json, LoanApplicationRequestDTO.class);
        var result = dealService.getLoanOffers(loanApplicationRequestDTO);
        var client = clientRepository.findClientByFirstName("IvanIvanIvan");
        var application = applicationRepository.findApplicationByClient(client);
        Assertions.assertEquals(result.size(), 4);
        Assertions.assertNotNull(client);
        Assertions.assertNotNull(application);
    }

    @Test
    public void checkCorrectPutLoanOffer() {
        String json = "{\"applicationId\": 1,\"requestedAmount\": 500000.00,\"totalAmount\": 500000.00,\"term\": 12,\"monthlyPayment\": 46989.91,\"rate\": 23,  \"isInsuranceEnabled\": false,\"isSalaryClient\": false}";
        LoanOfferDTO loanApplicationRequestDTO = new Gson().fromJson(json, LoanOfferDTO.class);
        dealService.selectLoanOffer(loanApplicationRequestDTO);
        var application = applicationRepository.findApplicationById(loanApplicationRequestDTO.getApplicationId());
        Assertions.assertEquals(application.getAppliedOffer(), "{\"applicationId\":1,\"requestedAmount\":500000.00,\"totalAmount\":500000.00,\"term\":12,\"monthlyPayment\":46989.91,\"rate\":23,\"isInsuranceEnabled\":false,\"isSalaryClient\":false}");
        Assertions.assertEquals(application.getCredit().getInsuranceEnable(), false);
        Assertions.assertEquals(application.getCredit().getSalaryClient(), false);
    }

    @Test
    public void checkCorrectCalculateLoan() {
        String json = "{\"gender\": \"MALE\",\"maritalStatus\": \"MARRIED\",\"dependentAmount\": 2,\"passportIssueDate\": \"1990-01-01\",\"passportIssueBranch\": \"Branch Name\",\"employment\": {\"employmentStatus\": \"SELF_EMPLOYED\",\"position\": \"TOP_MANAGER\",\"workExperienceTotal\": 60,\"workExperienceCurrent\": 12,    \"salary\": 50000},\"account\": \"1234567890\"}";
        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new Gson().fromJson(json, FinishRegistrationRequestDTO.class);
        CreditDTO creditDTO = dealService.calculateLoan(finishRegistrationRequestDTO, Long.parseLong("1"));
        Assertions.assertEquals(creditDTO.getPaymentSchedule().size(), creditDTO.getTerm());
    }
}
