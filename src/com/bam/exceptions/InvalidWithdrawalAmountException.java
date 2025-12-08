package com.bam.exceptions;

public class InvalidWithdrawalAmountException extends RuntimeException {
    public InvalidWithdrawalAmountException(String message) {
        super(message);
    }
}

