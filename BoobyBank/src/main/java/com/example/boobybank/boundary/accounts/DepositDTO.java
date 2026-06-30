package com.example.boobybank.boundary.accounts;

public class DepositDTO extends TransactionDTO{
    public DepositDTO(Double amount, String purpose, String timestamp) {
        super(amount, purpose, timestamp);
    }
}
