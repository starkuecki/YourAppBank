package com.example.boobybank.control.accounts;

import com.example.boobybank.control.shared.OnRead;
import com.example.boobybank.control.shared.OnWrite;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Account {
    @Null(groups = OnWrite.class)
    @NotNull(groups = OnRead.class)
    @Size(min = 22, max = 22)
    @Pattern(regexp = "[A-Z]{2}\\d{20}")
    private String iban;

    @Null(groups = OnWrite.class)
    @NotNull(groups = OnRead.class)
    private double balance;

    @Pattern(regexp = "current|savings")
    private String accountType;

    @NotNull
    private UUID ownerId;

    private List<Transaction> transactions;
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        if (transaction.getClass().equals(Deposit.class)) {
            balance = balance + transaction.getAmount();
        }
        if (transaction.getClass().equals(Withdrawal.class)) {
            balance = balance - transaction.getAmount();
        }
    }
}
