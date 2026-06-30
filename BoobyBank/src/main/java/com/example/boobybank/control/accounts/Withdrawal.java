package com.example.boobybank.control.accounts;

public class Withdrawal extends Transaction{
    public Withdrawal(Double amount, String purpose, String timestamp, String transactionType) {
        super(amount, purpose, timestamp, transactionType);
    }
}
