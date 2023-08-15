package com.example.deal.service;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(loanApplicationRequestDTO);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/conveyor/offers"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            try {
                Type listType = new TypeToken<ArrayList<LoanOfferDTO>>() {
                }.getType();
                ArrayList<LoanOfferDTO> resultList = gson.fromJson(responseBody, listType);
                return resultList;
            } catch (Exception e) {
                throw new RuntimeException(responseBody);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(scoringDataDTO);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/conveyor/calculation"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (responseBody.contains("Bad Request")) {
                throw new IllegalArgumentException(responseBody);
            }
            try {
                CreditDTO resultList = gson.fromJson(responseBody, CreditDTO.class);
                return resultList;
            } catch (Exception e) {
                throw new IllegalArgumentException(responseBody);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
