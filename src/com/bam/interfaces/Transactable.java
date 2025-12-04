package com.bam.interfaces;

public interface Transactable {
    boolean processTransaction(double amount, String type);
}
