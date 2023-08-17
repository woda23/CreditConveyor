package com.example.deal;

import com.example.deal.dto.EmploymentDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.example.deal.dto.enums.EmploymentStatus;
import com.example.deal.dto.enums.Gender;
import com.example.deal.dto.enums.MaritalStatus;
import com.example.deal.dto.enums.Position;
import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.mappers.ScoringDTOMapper;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.realization.DealServiceImpl;
import com.example.deal.service.realization.LoanServiceImpl;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DealServiceImplTests {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DealServiceImpl dealServiceImpl;
    @Autowired
    private CreditRepository creditRepository;
    @Test
    public void checkCorrectGetLoanOffers2() {
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


        LoanServiceImpl loanService = Mockito.mock(LoanServiceImpl.class);
        when(loanService.getLoanOffers(request)).thenReturn(response);
        List<LoanOfferDTO> loanOffers = loanService.getLoanOffers(request);
        Assertions.assertEquals(loanOffers.size(), 4);

        var client = dealServiceImpl.createClient(request);
        var application = dealServiceImpl.createApplication(client);
        Assertions.assertNotNull(client);
        Assertions.assertNotNull(application);
    }
    @Test
    public void checkCorrectPutLoanOffer() {
        String json = "{\"applicationId\": 1,\"requestedAmount\": 500000.00,\"totalAmount\": 500000.00,\"term\": 12,\"monthlyPayment\": 46989.91,\"rate\": 23,  \"isInsuranceEnabled\": false,\"isSalaryClient\": false}";
        LoanOfferDTO loanOfferDTO = new Gson().fromJson(json, LoanOfferDTO.class);
        dealServiceImpl.selectLoanOffer(loanOfferDTO);
        Application application = applicationRepository.findById(loanOfferDTO.getApplicationId()).get();
        Assertions.assertNotNull(application.getAppliedOffer());
        Assertions.assertEquals(application.getCredit().getInsuranceEnable(), false);
        Assertions.assertEquals(application.getCredit().getSalaryClient(), false);
    }

    @Test
    public void checkCorrectCalculateLoan() {
        FinishRegistrationRequestDTO request = FinishRegistrationRequestDTO.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(2)
                .passportIssueDate(LocalDate.of(1990, 1, 1))
                .passportIssueBranch("Branch Name")
                .employment(EmploymentDTO.builder()
                        .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                        .position(Position.TOP_MANAGER)
                        .workExperienceCurrent(12)
                        .workExperienceTotal(60)
                        .salary(BigDecimal.valueOf(50000))
                        .employerINN("inn")
                        .build())
                .build();


        LoanServiceImpl loanService = Mockito.mock(LoanServiceImpl.class);
        Application application = dealServiceImpl.getApplicationById(Long.valueOf(1));
        ScoringDataDTO scoringData = new ScoringDTOMapper().mapToEntity(request, application.getClient(),
                dealServiceImpl.getApplicationByClientId(application.getClient()));

        when(loanService.getCreditDTO(scoringData)).thenReturn(null);
        loanService.getCreditDTO(scoringData);

        loanService.getCreditDTO(scoringData);
        var credit = application.getCredit();
        credit.setAmount(new BigDecimal("500000.00"));
        credit.setTerm(12);
        credit.setMonthlyPayment(new BigDecimal("48155.20"));
        credit.setRate(new BigDecimal("27"));
        credit.setPsk(new BigDecimal("15.572"));
        dealServiceImpl.saveCredit(credit);

        Client client = clientRepository.findClientByFirstName("IvanIvanIvan").get();
        Credit creditFromDb = applicationRepository.findApplicationByClient(client).get().getCredit();
        Integer term = creditRepository.findById(creditFromDb.getId()).get().getTerm();
        Assertions.assertEquals(term, 12);
    }


    private static LoanApplicationRequestDTO getLoanApplicationRequestDTO() {
        LoanApplicationRequestDTO request = LoanApplicationRequestDTO.builder()
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
        return request;
    }
}
