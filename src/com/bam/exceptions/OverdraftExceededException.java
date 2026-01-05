package com.bam.exceptions;

public class OverdraftExceededException extends Exception {
    public OverdraftExceededException(String message) {
        super(message);
    }
}
