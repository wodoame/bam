package com.bam.exceptions;

/**
 * Indicates that an account number failed validation.
 */
public class InvalidAccountNumberException extends Exception {
    public InvalidAccountNumberException(String message) {
        super(message);
    }
}

