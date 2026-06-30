package com.example.boobybank.control.accounts;

import com.example.boobybank.entity.accounts.AccountEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountMapper {

    @NonNull
    public Account map(@NonNull AccountEntity entity) {
        return new Account(
                entity.getIban(),
                entity.getBalance(),
                entity.getAccountType(),
                entity.getOwnerId(),
                entity.getTransactions()
        );
    }


    @NonNull
    public AccountEntity map(String iban, @NonNull Account account) {
        return new AccountEntity(
                account.getIban(),
                account.getBalance(),
                account.getAccountType(),
                account.getOwnerId(),
                account.getTransactions()
        );
    }

}