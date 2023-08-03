package com.example.conveyor.controller;

import com.example.conveyor.exception.IllegalLoanRequestException;
import com.example.conveyor.wrapper.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
@Slf4j
public class ExceptionHandlingController {
    @ExceptionHandler({IllegalLoanRequestException.class, IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<ErrorResponse> handleInvalidLoanApplicationDataException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .build();
        ResponseEntity<ErrorResponse> errorResponseResponseEntity = new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        log.debug("handleInvalidLoanApplicationDataException(), ResponseEntity<ErrorResponse>: {}", errorResponseResponseEntity);
        return errorResponseResponseEntity;
    }
}
