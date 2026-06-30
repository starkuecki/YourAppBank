package com.example.boobybank.control.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class Withdrawal extends Transaction{
    public Withdrawal(Double amount, String purpose, String timestamp, String transactionType) {
        super(amount, purpose, timestamp, transactionType);
    }
}
