package com.example.application.service.realization;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.service.abstraction.DealServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class DealServiceImpl implements com.example.application.service.abstraction.DealService {
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        try {
            DealServiceClient client = configureFeign();
            List<LoanOfferDTO> response = client.sendLoanApplication(loanApplicationRequestDTO);
            log.info("getLoanOffers(), List<LoanOfferDTO>: {}", response);
            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void selectLoanOffer(LoanOfferDTO offer) {
        log.info("selectLoanOffer(), LoanOfferDTO: {}", offer);
        try {
            DealServiceClient client = configureFeign();
            client.sendLoanOffer(offer);
            log.info("selectLoanOffer(), request has been sent: {}", offer);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public DealServiceClient configureFeign() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapterService()) // Регистрируем адаптер для LocalDate
                .create();
        DealServiceClient client = Feign.builder()
                .encoder(new GsonEncoder(gson))
                .decoder(new GsonDecoder(gson))
                .target(DealServiceClient.class, "http://localhost:8090");
        return client;
    }
}
