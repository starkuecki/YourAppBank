package com.example.boobybank.boundary.accounts;

import java.util.ArrayList;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import com.example.boobybank.control.accounts.Account;
import com.example.boobybank.control.accounts.Deposit;
import com.example.boobybank.control.accounts.Withdrawal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

@Component
public class AccountDTOMapper {

    public AccountDTO map(Account account) {
        return new AccountDTO(
                account.getIban(),
                account.getBalance(),
                account.getAccountType(),
                account.getOwnerId().toString()
        );
    }


    public Account map(@NonNull AccountDTO accountDTO) {
        return new Account(
                accountDTO.getIban(),
                Double.valueOf(0),
                accountDTO.getAccountType(),
                UUID.fromString(accountDTO.getOwnerId()),
                new ArrayList<>()
        );
    }

    public Deposit map(DepositDTO depositDTO) {
        return new Deposit(
                depositDTO.getAmount(),
                depositDTO.getPurpose(),
                depositDTO.getTimestamp(),
                "deposit"
        );
    }

    public DepositDTO map(Deposit deposit) {
        return new DepositDTO(
                deposit.getAmount(),
                deposit.getPurpose(),
                deposit.getTimestamp()
        );
    }

    public Withdrawal map(WithdrawalDTO withdrawalDTO) {
        return new Withdrawal(
                withdrawalDTO.getAmount(),
                withdrawalDTO.getPurpose(),
                withdrawalDTO.getTimestamp(),
                "withdrawal"
        );
    }

    public WithdrawalDTO map(Withdrawal withdrawal) {
        return new WithdrawalDTO(
                withdrawal.getAmount(),
                withdrawal.getPurpose(),
                withdrawal.getTimestamp()
        );
    }
}