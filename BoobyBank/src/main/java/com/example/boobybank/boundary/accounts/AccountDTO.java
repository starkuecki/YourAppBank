package com.example.boobybank.boundary.accounts;

import com.example.boobybank.control.accounts.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AccountDTO {
    @Size(min = 22, max = 22)
    @Pattern(regexp = "[A-Z]{2}\\d{20}")
    private String iban;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Double balance;

    @Pattern(regexp = "current|savings")
    private String accountType;

    private String ownerId;

}
