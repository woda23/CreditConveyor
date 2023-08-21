package com.example.deal.mappers;

import com.example.deal.dto.FinishRegistrationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.example.deal.entity.Application;
import com.example.deal.entity.Client;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class ScoringDTOMapper {
    public ScoringDataDTO mapForScoringDataDTO(FinishRegistrationRequestDTO request, Client client, Application application) {
        var credit = application.getCredit();
        LoanOfferDTO offer = new Gson().fromJson(application.getAppliedOffer(), LoanOfferDTO.class);
        return ScoringDataDTO.builder()
                .amount(offer.getTotalAmount())
                .term(offer.getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(request.getGender())
                .birthdate(client.getBirthDate())
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
}
