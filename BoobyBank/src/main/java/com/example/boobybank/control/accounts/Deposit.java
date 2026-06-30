package com.example.boobybank.control.accounts;

public class Deposit extends Transaction{
    public Deposit(Double amount, String purpose, String timestamp, String transactionType) {
        super(amount, purpose, timestamp, transactionType);
    }
}
