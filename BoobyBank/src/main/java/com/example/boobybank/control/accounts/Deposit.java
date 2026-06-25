package com.example.boobybank.control.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class Deposit extends Transaction{
    public Deposit(Double amount, String purpose, String timestamp) {
        super(amount, purpose, timestamp);
    }
}
