package com.example.conveyor.exception;

public class IllegalLoanRequestException extends RuntimeException {
    public IllegalLoanRequestException(String message) {
        super(message);
    }
}
