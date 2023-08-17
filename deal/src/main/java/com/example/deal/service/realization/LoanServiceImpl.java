package com.example.deal.service.realization;

import com.example.deal.dto.CreditDTO;
import com.example.deal.dto.LoanApplicationRequestDTO;
import com.example.deal.dto.LoanOfferDTO;
import com.example.deal.dto.ScoringDataDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class LoanServiceImpl {

    @Value("${conveyor.url.offers}")
    private String loanOffersUrl;

    @Value("${conveyor.url.calculation}")
    private String creditOfferUrl;
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<LoanApplicationRequestDTO> requestEntity = new HttpEntity<>(loanApplicationRequestDTO, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(loanOffersUrl,
                    HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            try {
                Type listType = new TypeToken<ArrayList<LoanOfferDTO>>() {
                }.getType();
                ArrayList<LoanOfferDTO> resultList = new Gson().fromJson(responseBody, listType);
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<ScoringDataDTO> requestEntity = new HttpEntity<>(scoringDataDTO, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(creditOfferUrl,
                    HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            if (responseBody.contains("Bad Request")) {
                throw new IllegalArgumentException(responseBody);
            }
            try {
                CreditDTO creditDTO = new Gson().fromJson(responseBody, CreditDTO.class);
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
