package com.example.conveyor.service.realization;

import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.PaymentScheduleElement;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.exception.IllegalLoanRequestException;
import com.example.conveyor.service.abstraction.LoanScoringService;
import com.example.conveyor.service.abstraction.LoanService;
import com.example.conveyor.wrapper.AmountAndRateInRelationOfBooleanParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Сервис подсчета возможных условий и параметров кредита.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    @Value("${base.interest.rate}")
    private BigDecimal baseInterestRate;

    @Value("${insurance.price}")
    private BigDecimal insurancePrice;

    private final LoanScoringService loanScoringService;
    private static long loanOfferId = 0;

    /**
     * Рассчитывает возможные условия кредита.
     *
     * @param loanApplicationRequestDTO данные для расчета условий
     * @return 4 кредитных предложения от худшего к лучшему.
     */
    @Override
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("getLoanOffers(), LoanApplicationRequestDTO: {}", loanApplicationRequestDTO);
        List<LoanOfferDTO> loanOffers = new ArrayList<>();
        LoanOfferDTO loanOfferFF = getLoanOffer(loanApplicationRequestDTO, false, false);
        LoanOfferDTO loanOfferFT = getLoanOffer(loanApplicationRequestDTO, false, true);
        LoanOfferDTO loanOfferTF = getLoanOffer(loanApplicationRequestDTO, true, false);
        LoanOfferDTO loanOfferTT = getLoanOffer(loanApplicationRequestDTO, true, true);
        loanOffers.add(loanOfferFF);
        loanOffers.add(loanOfferFT);
        loanOffers.add(loanOfferTF);
        loanOffers.add(loanOfferTT);
        loanOffers.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());
        log.info("getLoanOffers(), List<LoanOfferDTO>: {}", loanOffers);
        return loanOffers;
    }


    /**
     * Рассчитывает возможные параметры кредита.
     *
     * @param scoringDataDTO данные для расчета параметров кредита.
     * @return результат расчета кредитных параметров.
     */
    @Override
    public CreditDTO calculateCredit(ScoringDataDTO scoringDataDTO) {
        log.info("calculateCredit(), ScoringDataDTO: {}", scoringDataDTO);
        var scoringVerdict = loanScoringService.calculateScore(scoringDataDTO);
        if (!scoringVerdict.isApproved()) {
            throw new IllegalLoanRequestException("You can't take out a loan");
        }

        BigDecimal rate = baseInterestRate.add(BigDecimal.valueOf(scoringVerdict.getRate()));
        Boolean isSalaryClient = scoringDataDTO.getIsSalaryClient();
        Boolean isInsuranceEnabled = scoringDataDTO.getIsInsuranceEnabled();
        var amountAndRate = getAmountAndRate(isInsuranceEnabled,
                isSalaryClient, scoringDataDTO.getAmount(), rate);

        BigDecimal finalRate = amountAndRate.getRate();
        BigDecimal finalAmount = amountAndRate.getAmount();
        BigDecimal monthlyPayment = calculateMonthlyPayment(finalAmount, finalRate, scoringDataDTO.getTerm());

        BigDecimal psk = ((monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm()))
                .divide(scoringDataDTO.getAmount(), 10, RoundingMode.HALF_UP)).subtract(BigDecimal.ONE))
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(scoringDataDTO.getTerm()).divide(BigDecimal.valueOf(12)));
        List<PaymentScheduleElement> paymentScheduleElements =
                calculatePaymentSchedule(finalAmount, finalRate, scoringDataDTO.getTerm());

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(finalAmount)
                .term(scoringDataDTO.getTerm())
                .rate(finalRate)
                .psk(psk.setScale(3, RoundingMode.HALF_UP))
                .monthlyPayment(monthlyPayment)
                .paymentSchedule(paymentScheduleElements)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
        log.info("calculateCredit(), CreditDTO: {}", creditDTO);
        return creditDTO;
    }

    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO,
                                      Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        var amountAndRate = getAmountAndRate(isInsuranceEnabled, isSalaryClient,
                loanApplicationRequestDTO.getAmount(), baseInterestRate);
        LoanOfferDTO offerDTO = LoanOfferDTO.builder()
                .applicationId(loanOfferId++)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .term(loanApplicationRequestDTO.getTerm())
                .rate(amountAndRate.getRate())
                .totalAmount(amountAndRate.getAmount())
                .build();
        BigDecimal monthlyPaymentNoInsNoSal = calculateMonthlyPayment(
                offerDTO.getTotalAmount(), offerDTO.getRate(), offerDTO.getTerm());
        offerDTO.setMonthlyPayment(monthlyPaymentNoInsNoSal);
        return offerDTO;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, BigDecimal interestRate, int term) {
        BigDecimal rate = getMonthRate(interestRate);
        return loanAmount
                .multiply(rate)
                .multiply((BigDecimal.ONE.add(rate)).pow(term))
                .divide(((BigDecimal.ONE.add(rate)).pow(term)).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    private List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal loanAmount, BigDecimal interestRate,
                                                                  int term) {
        BigDecimal rate = getMonthRate(interestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(loanAmount, interestRate, term);
        BigDecimal remainingAmount = loanAmount;
        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();
        double threshold = 0.001;
        for (int i = 1; i <= term; i++) {
            BigDecimal interest = remainingAmount.multiply(rate);
            BigDecimal principal = monthlyPayment.subtract(interest);
            remainingAmount = remainingAmount.subtract(principal);
            if (remainingAmount.doubleValue() < threshold) {
                remainingAmount = BigDecimal.valueOf(0.0);
            }
            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                    .number(i)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(interest.add(principal).setScale(3, RoundingMode.HALF_UP))
                    .debtPayment(principal.setScale(3, RoundingMode.HALF_UP))
                    .interestPayment(interest.setScale(3, RoundingMode.HALF_UP))
                    .remainingDebt(remainingAmount.setScale(3, RoundingMode.HALF_UP))
                    .build();
            paymentScheduleElements.add(paymentScheduleElement);
        }
        return paymentScheduleElements;
    }

    private BigDecimal getMonthRate(BigDecimal interestRate) {
        return interestRate.divide(BigDecimal.valueOf(1200), 3, RoundingMode.HALF_UP);
    }

    private AmountAndRateInRelationOfBooleanParams getAmountAndRate(Boolean isInsuranceEnabled, Boolean isSalaryClient,
                                                                    BigDecimal amount, BigDecimal rate) {
        if (!isInsuranceEnabled && !isSalaryClient)
            return new AmountAndRateInRelationOfBooleanParams(amount, rate.add(BigDecimal.valueOf(5)));
        if (!isInsuranceEnabled)
            return new AmountAndRateInRelationOfBooleanParams(amount, rate.add(BigDecimal.ONE));
        if (!isSalaryClient)
            return new AmountAndRateInRelationOfBooleanParams(amount.add(insurancePrice),
                    rate.subtract(BigDecimal.valueOf(3)));
        return new AmountAndRateInRelationOfBooleanParams(amount.add(insurancePrice),
                rate.subtract(BigDecimal.valueOf(5)));
    }
}
