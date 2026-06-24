package com.example.boobybank.boundary.accounts;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SavingsAccountDTO extends AccountDTO {

    @Min(0)
    @Max(100)
    private Float interestRate;


    SavingsAccountDTO(
            String iban,
            double balance,
            Float interestRate) {
        super(iban, balance, "savings");

        this.interestRate = interestRate;
    }


    public static SavingsAccountDTO newFrom(SavingsAccountDTO account) {
        return new SavingsAccountDTO(
                account.getIban(),
                0.0,
                account.getInterestRate()
        );
    }


    public static SavingsAccountDTO copyFrom(SavingsAccountDTO account) {
        return new SavingsAccountDTO(
                account.getIban(),
                account.getBalance(),
                account.getInterestRate()
        );
    }

}
