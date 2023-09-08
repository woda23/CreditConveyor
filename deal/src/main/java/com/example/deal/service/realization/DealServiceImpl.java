package com.example.deal.service.realization;

import com.example.deal.config.Producer;
import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.EmailMessage;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.example.deal.dto.enums.ApplicationStatus;
import com.example.deal.dto.enums.ChangeType;
import com.example.deal.dto.enums.EmailMessageTheme;
import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Employment;
import com.example.deal.entity.Passport;
import com.example.deal.entity.PaymentScheduleElementEntity;
import com.example.deal.entity.jsonb.EmploymentData;
import com.example.deal.entity.jsonb.StatusHistory;
import com.example.deal.mappers.ApplicationMapper;
import com.example.deal.mappers.ClientMapper;
import com.example.deal.mappers.CreditMapper;
import com.example.deal.mappers.EmploymentMapper;
import com.example.deal.mappers.PassportMapper;
import com.example.deal.mappers.ScoringDTOMapper;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.EmploymentRepository;
import com.example.deal.repository.PassportRepository;
import com.example.deal.service.abstraction.DealService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DealServiceImpl implements DealService {
    private final LoanServiceImpl loanServiceImpl;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final PassportRepository passportRepository;
    private final CreditRepository creditRepository;
    private final EmploymentRepository employmentRepository;
    private final ApplicationMapper applicationMapper;
    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;
    private final PassportMapper passportMapper;
    private final ScoringDTOMapper scoringDTOMapper;
    private final EmploymentMapper employmentMapper;
    private final Producer producer;

    public void sentMessage(String topic, Long applicationId) {
        var address = getApplicationById(applicationId).getClient().getEmail();
        var emailMessage = EmailMessage.builder()
                .address(address)
                .applicationId(applicationId)
                .build();
        switch (topic) {
            case "finish-registration" -> emailMessage.setTheme(EmailMessageTheme.FINISH_REGISTRATION);
            case "create-documents" -> emailMessage.setTheme(EmailMessageTheme.CREATE_DOCUMENTS);
            case "send-documents" -> emailMessage.setTheme(EmailMessageTheme.SEND_DOCUMENTS);
            case "send-ses" -> emailMessage.setTheme(EmailMessageTheme.SEND_SES);
            case "credit-issued" -> emailMessage.setTheme(EmailMessageTheme.CREDIT_ISSUED);
            case "application-denied" -> emailMessage.setTheme(EmailMessageTheme.APPLICATION_DENIED);
        }
        producer.sendMessage(emailMessage, topic);
    }
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO request) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", request);
        Client client = createClient(request);
        Application application = createApplication(client);
        List<LoanOfferDTO> loanOffers = loanServiceImpl.getLoanOffers(request);
        loanOffers.forEach(offer -> offer.setApplicationId(application.getId()));
        log.info("getLoanOffers(), List<LoanOfferDTO>: {}", loanOffers);
        return loanOffers;
    }

    public void selectLoanOffer(LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        Application application = getApplicationById(offer.getApplicationId());
        updateApplicationWithSelectedOffer(application, offer);
        Credit credit = createCredit(offer);
        application.setCredit(credit);
        saveApplication(application);
        log.info("selectLoanOffer(), Application: {}", application);
    }

    public CreditDTO calculateLoan(FinishRegistrationRequestDTO request, Long applicationId) {
        log.info("calculateLoan(), FinishRegistrationRequestDTO: {}, Long: {}", request, applicationId);
        Application application = getApplicationById(applicationId);
        Client client = application.getClient();
        EmploymentData employmentData = employmentMapper.mapToEmploymentData(request.getEmployment());
        var employment = client.getEmployment();
        employment.setEmploymentData(employmentData);
        saveEmployment(employment);
        ScoringDataDTO scoringData = scoringDTOMapper.mapForScoringDataDTO(request, client,
                getApplicationByClientId(client));
        var result = loanServiceImpl.getCreditDTO(scoringData);
        var credit = application.getCredit();
        credit.setAmount(result.getAmount());
        credit.setTerm(result.getTerm());
        credit.setPsk(result.getPsk());
        credit.setRate(result.getRate());
        credit.setMonthlyPayment(result.getMonthlyPayment());
        List<PaymentScheduleElementEntity> paymentScheduleElementEntities = result.getPaymentSchedule().stream()
                .map(PaymentScheduleElementEntity::new)
                .collect(Collectors.toList());
        credit.setPaymentSchedule(paymentScheduleElementEntities);
        saveCredit(credit);

        log.info("calculateLoan(), CreditDTO: {}", result);
        return result;
    }

    public Client createClient(LoanApplicationRequestDTO request) {
        log.info("createClient(), LoanApplicationRequestDTO: {}", request);
        Client client = clientMapper.mapToEntity(request);
        saveEmployment(client.getEmployment());
        var passport = savePassport(passportMapper.mapToEntity(request));
        client.setPassport(passport);
        log.info("createClient(), Client: {}", client);
        return saveClient(client);
    }

    public Application createApplication(Client client) {
        log.info("createApplication(), Client: {}", client);
        return saveApplication(applicationMapper.mapToApplication(client));
    }

    public Credit createCredit(LoanOfferDTO request) {
        log.info("createCredit(), LoanOfferDTO: {}", request);
        return saveCredit(creditMapper.mapToEntity(request));
    }

    public void updateApplicationWithSelectedOffer(Application application, LoanOfferDTO offer) {
        log.info("updateApplicationWithSelectedOffer(), Application: {}, LoanOfferDTO: {}", application, offer);
        var status = ApplicationStatus.PREAPPROVAL;
        application.setStatus(status.toString());
        var gson = new Gson();
        StatusHistory currentStatusHistory = new StatusHistory(status, Timestamp.valueOf(LocalDate.now().atStartOfDay()),
                ChangeType.AUTOMATIC);
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
        log.info("createCredit(), Application: {}", application);
    }

    public Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow();
    }

    public Application getApplicationByClientId(Client client) {
        return applicationRepository.findApplicationByClient(client).get();
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    public Credit saveCredit(Credit credit) {
        return creditRepository.save(credit);
    }

    public Employment saveEmployment(Employment employment) {
        return employmentRepository.save(employment);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Passport savePassport(Passport passport) {
        return passportRepository.save(passport);
    }
}
