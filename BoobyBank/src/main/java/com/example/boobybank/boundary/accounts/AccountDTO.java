package com.example.boobybank.boundary.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Size(min = 22, max = 22)
    @Pattern(regexp = "[A-Z]{2}\\d{20}")
    private String iban;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double balance;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Pattern(regexp = "current|savings")
    private String accountType;
}
