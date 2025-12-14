package com.bam.exceptions;

/**
 * Indicates that an email address failed validation.
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}

