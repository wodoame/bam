package com.bam.exceptions;

public class InvalidDepositAmountException extends RuntimeException {
    public InvalidDepositAmountException(String message) {
        super(message);
    }
}

