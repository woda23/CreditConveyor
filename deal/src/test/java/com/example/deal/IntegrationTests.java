package com.example.deal;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.entity.Application;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.realization.DealServiceImpl;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = IntegrationTests.Initializer.class)
public class IntegrationTests {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("deal")
            .withUsername("postgres")
            .withPassword("password")
            .withExposedPorts(5432).withInitScript("db.sql");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    String.format("spring.datasource.url=jdbc:postgresql://localhost:%d/deal", postgres.getFirstMappedPort()),
                    "spring.datasource.username=postgres",
                    "spring.datasource.password=password"
            ).applyTo(applicationContext);
        }
    }

    static {
        postgres.start();
    }

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DealServiceImpl dealServiceImpl;

    @Test
    public void checkCorrectGetLoanOffers() {
        String json = "{\"amount\": 500000.00,\"term\": 12,\"firstName\": \"IvanIvanIvan\",\"lastName\": \"Ivanov\",\"middleName\": \"Ivanovich\",\"email\": \"ivanov@mail.ru\",\"birthdate\":\"1990-01-01\",\"passportSeries\": \"1234\",\"passportNumber\": \"123456\"}";
        LoanApplicationRequestDTO loanApplicationRequestDTO = new Gson().fromJson(json, LoanApplicationRequestDTO.class);
        var result = dealServiceImpl.getLoanOffers(loanApplicationRequestDTO);
        var client = clientRepository.findClientByFirstName("IvanIvanIvan").get();
        var application = applicationRepository.findApplicationByClient(client);
        Assertions.assertEquals(result.size(), 4);
        Assertions.assertNotNull(client);
        Assertions.assertNotNull(application);
    }

    @Test
    public void checkCorrectPutLoanOffer() {
        String json = "{\"applicationId\": 1,\"requestedAmount\": 500000.00,\"totalAmount\": 500000.00,\"term\": 12,\"monthlyPayment\": 46989.91,\"rate\": 23,  \"isInsuranceEnabled\": false,\"isSalaryClient\": false}";
        LoanOfferDTO loanApplicationRequestDTO = new Gson().fromJson(json, LoanOfferDTO.class);
        dealServiceImpl.selectLoanOffer(loanApplicationRequestDTO);
        Application application = applicationRepository.findById(loanApplicationRequestDTO.getApplicationId()).get();
        Assertions.assertEquals(application.getAppliedOffer(), "{\"applicationId\":1,\"requestedAmount\":500000.00,\"totalAmount\":500000.00,\"term\":12,\"monthlyPayment\":46989.91,\"rate\":23,\"isInsuranceEnabled\":false,\"isSalaryClient\":false}");
        Assertions.assertEquals(application.getCredit().getInsuranceEnable(), false);
        Assertions.assertEquals(application.getCredit().getSalaryClient(), false);
    }

    @Test
    public void checkCorrectCalculateLoan() {
        String json = "{\"gender\": \"MALE\",\"maritalStatus\": \"MARRIED\",\"dependentAmount\": 2,\"passportIssueDate\": \"1990-01-01\",\"passportIssueBranch\": \"Branch Name\",\"employment\": {\"employmentStatus\": \"SELF_EMPLOYED\",\"position\": \"TOP_MANAGER\",\"workExperienceTotal\": 60,\"workExperienceCurrent\": 12,    \"salary\": 50000},\"account\": \"1234567890\"}";
        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new Gson().fromJson(json, FinishRegistrationRequestDTO.class);
        CreditDTO creditDTO = dealServiceImpl.calculateLoan(finishRegistrationRequestDTO, Long.parseLong("1"));
        Assertions.assertEquals(creditDTO.getPaymentSchedule().size(), creditDTO.getTerm());
    }
}
