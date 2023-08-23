package com.example.application.service.realization;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.service.abstraction.DealService;
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
public class DealServiceImpl implements DealService {

    @Value("${deal.url.application}")
    private String loanOffersUrl;

    @Value("${deal.url.offer}")
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

    public void selectLoanOffer(LoanOfferDTO offer) {

    }
}
