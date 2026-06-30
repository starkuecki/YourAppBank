package com.example.boobybank.boundary.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {

    private Double amount;

    private String purpose;

    private String timestamp;
}
