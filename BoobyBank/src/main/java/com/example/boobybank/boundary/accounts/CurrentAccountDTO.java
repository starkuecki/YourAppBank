package com.example.boobybank.boundary.accounts;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CurrentAccountDTO extends AccountDTO {

    @Min(0)
    private Double overdraftLimit;
    @Min(0)
    @Max(100)
    private Float overdraftInterestRate;


    CurrentAccountDTO(
            String iban,
            double balance,
            Double overdraftLimit,
            Float overdraftInterestRate) {
        super(iban, balance, "current");

        this.overdraftLimit = overdraftLimit;
        this.overdraftInterestRate = overdraftInterestRate;
    }


    public static CurrentAccountDTO newFrom(CurrentAccountDTO account) {
        return new CurrentAccountDTO(
                account.getIban(),
                0.0,
                account.getOverdraftLimit(),
                account.getOverdraftInterestRate()
        );
    }


    public static CurrentAccountDTO copyFrom(CurrentAccountDTO account) {
        return new CurrentAccountDTO(
                account.getIban(),
                account.getBalance(),
                account.getOverdraftLimit(),
                account.getOverdraftInterestRate()
        );
    }

}