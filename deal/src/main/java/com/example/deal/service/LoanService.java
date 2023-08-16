package com.example.deal.service;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LoanService {

    @Value("${loan.offers.url}")
    private String loanOffersUrl;

    @Value("${credit.offer.url}")
    private String creditOfferUrl;
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        try {
            Gson gson = new Gson();
            String json = gson.toJson(loanApplicationRequestDTO);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loanOffersUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            try {
                Type listType = new TypeToken<ArrayList<LoanOfferDTO>>() {
                }.getType();
                ArrayList<LoanOfferDTO> resultList = gson.fromJson(responseBody, listType);
                log.info("getLoanOffers(), List<LoanOfferDTO>: {}", resultList);
                return resultList;
            } catch (Exception e) {
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO) {
        log.info("getCreditDTO(), ScoringDataDTO: {}", scoringDataDTO);
        try {
            Gson gson = new Gson();
            String json = gson.toJson(scoringDataDTO);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(creditOfferUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (responseBody.contains("Bad Request")) {
                throw new IllegalArgumentException(responseBody);
            }
            try {
                CreditDTO creditDTO = gson.fromJson(responseBody, CreditDTO.class);
                log.info("getCreditDTO(), CreditDTO: {}", creditDTO);
                return creditDTO;
            } catch (Exception e) {
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
