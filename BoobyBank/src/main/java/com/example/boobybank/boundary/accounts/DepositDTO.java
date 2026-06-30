package com.example.boobybank.boundary.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

public class DepositDTO extends TransactionDTO{
    public DepositDTO(Double amount, String purpose, String timestamp) {
        super(amount, purpose, timestamp);
    }
}
