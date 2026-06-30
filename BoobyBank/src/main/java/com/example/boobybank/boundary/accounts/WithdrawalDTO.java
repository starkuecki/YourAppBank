package com.example.boobybank.boundary.accounts;

public class WithdrawalDTO extends TransactionDTO{
    public WithdrawalDTO(Double amount, String purpose, String timestamp) {
        super(amount, purpose, timestamp);
    }
}
