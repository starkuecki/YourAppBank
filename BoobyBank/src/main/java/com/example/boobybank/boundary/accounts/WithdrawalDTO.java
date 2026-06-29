package com.example.boobybank.boundary.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class WithdrawalDTO extends TransactionDTO{
    public WithdrawalDTO(Double amount, String purpose, String timestamp) {
        super(amount, purpose, timestamp);
    }
}
