package com.example.boobybank.control.accounts;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @NotNull
    private Double amount;

    @NotNull
    private String purpose;

    @NotNull
    private String timestamp;

    @NotNull
    private String transactionType;
}
