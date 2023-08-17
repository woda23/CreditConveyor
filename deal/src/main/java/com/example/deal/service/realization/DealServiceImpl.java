package com.example.deal.service.realization;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.example.deal.dto.enums.ApplicationStatus;
import com.example.deal.dto.enums.ChangeType;
import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Passport;
import com.example.deal.entity.PaymentScheduleElementEntity;
import com.example.deal.entity.StatusHistory;
import com.example.deal.mappers.ClientMapper;
import com.example.deal.mappers.CreditMapper;
import com.example.deal.mappers.PassportMapper;
import com.example.deal.mappers.ScoringDTOMapper;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.PassportRepository;
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
public class DealServiceImpl {

    private final LoanServiceImpl loanServiceImpl;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final PassportRepository passportRepository;

    private final CreditRepository creditRepository;
    @Transactional
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO request) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", request);
        Client client = createClient(request);
        Application application = createApplication(client);
        List<LoanOfferDTO> loanOffers = loanServiceImpl.getLoanOffers(request);
        loanOffers.forEach(offer -> offer.setApplicationId(application.getId()));
        log.info("getLoanOffers(), List<LoanOfferDTO>: {}", loanOffers);
        return loanOffers;
    }
    @Transactional
    public void selectLoanOffer(LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        Application application = getApplicationById(offer.getApplicationId());
        updateApplicationWithSelectedOffer(application, offer);
        Credit credit = createCredit(offer);
        application.setCredit(credit);
        saveApplication(application);
        log.info("selectLoanOffer(), Application: {}", application);
    }

    @Transactional
    public CreditDTO calculateLoan(FinishRegistrationRequestDTO request, Long applicationId) {
        log.info("calculateLoan(), FinishRegistrationRequestDTO: {}, Long: {}", request, applicationId);
        Application application = getApplicationById(applicationId);
        ScoringDataDTO scoringData = createScoringData(request, application.getClient());
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

    private Client createClient(LoanApplicationRequestDTO request) {
        Client client = new ClientMapper().mapToEntity(request);
        var passport = savePassport(new PassportMapper().mapToEntity(request));
        client.setPassport(passport);
        return saveClient(client);
    }

    private Application createApplication(Client client) {
        return saveApplication(new ClientMapper().entityToMap(client));
    }

    private Credit createCredit(LoanOfferDTO request) {
        return saveCredit(new CreditMapper().mapToEntity(request));
    }

    private void updateApplicationWithSelectedOffer(Application application, LoanOfferDTO offer) {
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
    }

    private ScoringDataDTO createScoringData(FinishRegistrationRequestDTO request, Client client) {
        var application = getApplicationByClientId(client);
        var credit = application.getCredit();
        LoanOfferDTO offer = new Gson().fromJson(application.getAppliedOffer(), LoanOfferDTO.class);
        return new ScoringDTOMapper().mapToEntity(request, client, offer, credit);
    }

    private Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow();
    }

    private Application getApplicationByClientId(Client client) {
        return applicationRepository.findApplicationByClient(client).get();
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
