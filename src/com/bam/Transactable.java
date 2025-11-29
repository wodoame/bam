package com.bam;

public interface Transactable {
    boolean processTransaction(double amount, String type);
}
