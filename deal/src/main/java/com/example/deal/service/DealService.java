package com.example.deal.service;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.PassportData;
import com.example.deal.dto.ScoringDataDTO;
import com.example.deal.dto.entity.Application;
import com.example.deal.dto.entity.Client;
import com.example.deal.dto.entity.Credit;
import com.example.deal.dto.enums.ApplicationStatus;
import com.example.deal.dto.enums.ChangeType;
import com.example.deal.dto.jsonb.Passport;
import com.example.deal.dto.jsonb.StatusHistory;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.PassportRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DealService {

    private final LoanService loanService;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final PassportRepository passportRepository;

    private final CreditRepository creditRepository;
    @Autowired
    public DealService(LoanService loanService, ClientRepository clientRepository,
                       ApplicationRepository applicationRepository,
                       PassportRepository passportRepository,
                       CreditRepository creditRepository) {
        this.loanService = loanService;
        this.clientRepository = clientRepository;
        this.applicationRepository = applicationRepository;
        this.passportRepository = passportRepository;
        this.creditRepository = creditRepository;
    }

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO request) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", request);
        Client client = createClient(request);
        Application application = createApplication(client);
        List<LoanOfferDTO> loanOffers = loanService.getLoanOffers(request);
        loanOffers.forEach(offer -> offer.setApplicationId(application.getId()));
        return loanOffers;
    }

    public void selectLoanOffer(LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        Application application = getApplicationById(offer.getApplicationId());
        updateApplicationWithSelectedOffer(application, offer);
        Credit credit = createCredit(offer);
        application.setCredit(credit);
        saveApplication(application);
    }

    public CreditDTO calculateLoan(FinishRegistrationRequestDTO request, Long applicationId) {
        log.info("calculateLoan(), FinishRegistrationRequestDTO: {}, Long: {}", request, applicationId);
        Application application = getApplicationById(applicationId);
        ScoringDataDTO scoringData = createScoringData(request, application.getClient());
        var result = loanService.getCreditDTO(scoringData);
        var credit = application.getCredit();
        credit.setAmount(result.getAmount());
        credit.setTerm(result.getTerm());
        credit.setPsk(result.getPsk());
        credit.setRate(result.getRate());
        credit.setMonthlyPayment(result.getMonthlyPayment());
        credit.setPaymentSchedule(result.getPaymentSchedule());
        saveCredit(credit);
        return result;
    }

    private Client createClient(LoanApplicationRequestDTO request) {
        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .birthDate(LocalDate.parse(request.getBirthdate()))
                .build();
        var passport = savePassport(new Passport(
                new PassportData(request.getPassportSeries(), request.getPassportNumber(),null, null)));
        client.setPassport(passport);
        return saveClient(client);
    }

    private Application createApplication(Client client) {
        Application application = Application.builder()
                .client(client)
                .build();
        return saveApplication(application);
    }

    private Credit createCredit(LoanOfferDTO request) {
        Credit credit = Credit.builder()
                .insuranceEnable(request.getIsInsuranceEnabled())
                .salaryClient(request.getIsSalaryClient())
                .build();
        return saveCredit(credit);
    }

    private void updateApplicationWithSelectedOffer(Application application, LoanOfferDTO offer) {
        var status = ApplicationStatus.PREAPPROVAL;
        application.setStatus(status.toString());
        var gson = new Gson();
        StatusHistory currentStatusHistory = new StatusHistory(status, LocalDate.now().toString(), ChangeType.AUTOMATIC);
        if (application.getStatusHistory() == null) {
            ArrayList<StatusHistory> newList = new ArrayList<>();
            newList.add(currentStatusHistory);
            application.setStatusHistory(newList);
        }
        else {
            ArrayList<StatusHistory> resultList = application.getStatusHistory();
            resultList.add(currentStatusHistory);
            application.setStatusHistory(resultList);
        }
        application.setAppliedOffer(gson.toJson(offer));
        saveApplication(application);
    }

    private ScoringDataDTO createScoringData(FinishRegistrationRequestDTO request, Client client) {
        var application = getApplicationByClientId(client);
        var credit = application.getCredit();
        LoanOfferDTO offer = new Gson().fromJson(application.getAppliedOffer(), LoanOfferDTO.class);
        return ScoringDataDTO.builder()
                .amount(offer.getTotalAmount())
                .term(offer.getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(request.getGender())
                .birthdate(client.getBirthDate().toString())
                .passportSeries(client.getPassport().passportData.series)
                .passportNumber(client.getPassport().passportData.number)
                .passportIssueDate(request.getPassportIssueDate().toString())
                .passportIssueBranch(request.getPassportIssueBranch())
                .maritalStatus(request.getMaritalStatus())
                .dependentAmount(request.getDependentAmount())
                .employment(request.getEmployment())
                .account(request.getAccount())
                .isInsuranceEnabled(credit.getInsuranceEnable())
                .isSalaryClient(credit.getSalaryClient())
                .build();
    }

    private Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow();
    }

    private Application getApplicationByClientId(Client client) {
        return applicationRepository.findApplicationByClient(client);
    }

    private Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    private Credit saveCredit(Credit credit) {
        return creditRepository.save(credit);
    }

    private Client saveClient(Client client) {
        return clientRepository.save(client);
    }
    private Passport savePassport(Passport passport) {
        return passportRepository.save(passport);
    }
}
