package com.example.creditconveyor.exception;

public class IllegalLoanRequestException extends RuntimeException {
    public IllegalLoanRequestException(String message) {
        super(message);
    }
}
