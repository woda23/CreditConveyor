package com.example.conveyor.service.realization;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.exception.IllegalLoanRequestException;
import com.example.conveyor.service.abstraction.LoanPreScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сервис для предварительной проверки заявки на кредит перед ее обработкой.
 */
@Service
public class LoanPreScoringServiceImpl implements LoanPreScoringService {
    private static final String NAME_REGEX = "[a-zA-Z]{2,30}";
    private static final String EMAIL_REGEX = "[\\w.]{2,50}@[\\w.]{2,20}";
    private static final String PASSPORT_SERIES_REGEX = "\\d{4}";
    private static final String PASSPORT_NUMBER_REGEX = "\\d{6}";

    private static final Logger log = LoggerFactory.getLogger(LoanPreScoringServiceImpl.class);
    /**
     * Проверяет заявку на кредит на соответствие правилам прескоринга.
     *
     * @param loanApplicationRequestDTO заявка на кредит
     * @throws IllegalLoanRequestException если хотя бы одно поле не соответствует правилам прескоринга
     */
    @Override
    public void preScoreLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        if (loanApplicationRequestDTO == null) {
            throw new NullPointerException("Loan application request is null");
        }

        var resultExceptionMessage =
        validateName(loanApplicationRequestDTO.getFirstName(), "First name") +
        validateName(loanApplicationRequestDTO.getLastName(), "Last name") +
        validateMiddleName(loanApplicationRequestDTO.getMiddleName()) +
        validateLoanAmount(loanApplicationRequestDTO.getAmount()) +
        validateLoanTerm(loanApplicationRequestDTO.getTerm()) +
        validateBirthDate(loanApplicationRequestDTO.getBirthdate()) +
        validateEmail(loanApplicationRequestDTO.getEmail()) +
        validatePassportSeries(loanApplicationRequestDTO.getPassportSeries()) +
        validatePassportNumber(loanApplicationRequestDTO.getPassportNumber());
        if (!resultExceptionMessage.isEmpty()) {
            throw new IllegalArgumentException(resultExceptionMessage);
        }
        log.info("Prescore passed successfully");
    }

    private String validateName(String name, String field) throws IllegalLoanRequestException {
        if (name == null || !name.matches(NAME_REGEX)) {
            return (field + " is invalid ");
        }
        return "";
    }
    private String validateMiddleName(String name) throws IllegalLoanRequestException {
        if (name != null && !name.matches(NAME_REGEX)) {
            return ("Middle name is invalid ");
        }
        return "";
    }

    private String validateLoanAmount(BigDecimal amount) throws IllegalLoanRequestException {
        if (amount == null || amount.compareTo(BigDecimal.valueOf(10000)) < 0) {
            return "Loan amount is invalid ";
        }
        return "";
    }

    private String validateLoanTerm(Integer term) throws IllegalLoanRequestException {
        if (term == null || term < 6) {
            return "Loan term is invalid ";
        }
        return "";
    }

    private String validateBirthDate(LocalDate birthdate) throws IllegalLoanRequestException {
        if (birthdate == null || birthdate.plusYears(18).isAfter(LocalDate.now())) {
            return "Birth date is invalid ";
        }
        return "";
    }

    private String validateEmail(String email) throws IllegalLoanRequestException {
        if (email == null || !email.matches(EMAIL_REGEX)) {
            return "Email is invalid ";
        }
        return "";
    }

    private String validatePassportSeries(String passportSeries) throws IllegalLoanRequestException {
        if (passportSeries == null || !passportSeries.matches(PASSPORT_SERIES_REGEX)) {
            return "Passport series is invalid ";
        }
        return "";
    }

    private String validatePassportNumber(String passportNumber) throws IllegalLoanRequestException {
        if (passportNumber == null || !passportNumber.matches(PASSPORT_NUMBER_REGEX)) {
            return "Passport number is invalid ";
        }
        return "";
    }
}
