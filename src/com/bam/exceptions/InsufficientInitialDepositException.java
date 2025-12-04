package com.bam.exceptions;

public class InsufficientInitialDepositException extends RuntimeException {
    public InsufficientInitialDepositException(String message) {
        super(message);
    }
}

