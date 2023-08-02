package com.example.conveyor.service.realization;

import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.service.abstraction.LoanScoringService;
import com.example.conveyor.wrapper.ScoringResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сервис для проверки заявки на кредит перед ее обработкой.
 */
@Service
public class LoanScoringServiceImpl implements LoanScoringService {
    private static final Logger log = LoggerFactory.getLogger(LoanScoringServiceImpl.class);

    /**
     * Рассчитывает кредитный рейтинг заемщика на основе предоставленных данных.
     *
     * @param scoringData данные для расчета кредитного рейтинга
     * @return результат расчета кредитного рейтинга
     */
    @Override
    public ScoringResult calculateScore(ScoringDataDTO scoringData) {
        ScoringResult result = new ScoringResult();

        // Рабочий статус
        switch (scoringData.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED -> {
                result.setApproved(false);
                return result;
            }
            case SELF_EMPLOYED -> result.setRate(result.getRate() + 1);
            case BUSINESS_OWNER -> result.setRate(result.getRate() + 3);
        }

        // Позиция на работе
        switch (scoringData.getEmployment().getPosition()) {
            case MIDDLE_MANAGER -> result.setRate(result.getRate() + 1);
            case TOP_MANAGER -> result.setRate(result.getRate() + 4);
        }

        // Сумма займа
        if (scoringData.getAmount().compareTo(BigDecimal.valueOf(20).multiply(scoringData.getEmployment().getSalary())) > 0) {
            result.setApproved(false);
            return result;
        }

        // Семейное положение
        switch (scoringData.getMaritalStatus()) {
            case MARRIED -> result.setRate(result.getRate() - 1);
            case DIVORCED -> result.setRate(result.getRate() + 1);
        }

        // Количество иждивенцев
        if (scoringData.getDependentAmount() > 1) {
            result.setRate(result.getRate() + 1);
        }

        // Возраст
        int age = LocalDate.now().getYear() - scoringData.getBirthdate().getYear();
        if (age < 20 || age > 60) {
            result.setApproved(false);
            return result;
        }

        // Пол
        switch (scoringData.getGender()) {
            case FEMALE -> {
                if (age >= 35) {
                    result.setRate(result.getRate() - 1);
                }
            }
            case MALE -> {
                if (age >= 30 && age <= 55) {
                    result.setRate(result.getRate() - 1);
                }
            }
            case NON_BINARY -> result.setRate(result.getRate() + 30);
        }

        // Стаж работы
        if (scoringData.getEmployment().getWorkExperienceTotal() < 12 ||
                scoringData.getEmployment().getWorkExperienceCurrent() < 3) {
            result.setApproved(false);
            return result;
        }

        // Результат
        result.setApproved(true);
        log.info("Score passed successfully, set approved for request, calculate new rate");
        return result;
    }
}

