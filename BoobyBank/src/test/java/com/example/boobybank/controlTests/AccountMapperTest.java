package com.example.boobybank.controlTests;

import com.example.boobybank.control.accounts.Account;
import com.example.boobybank.control.accounts.AccountMapper;
import com.example.boobybank.control.accounts.Deposit;
import com.example.boobybank.control.accounts.Transaction;
import com.example.boobybank.entity.accounts.AccountEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapper();

    @Test
    void map_entityToAccount_copiesAllFields() {
        UUID ownerId = UUID.randomUUID();
        List<Transaction> transactions = List.of(new Deposit(50.0, "p", "t", "deposit"));
        AccountEntity entity = new AccountEntity("DE00000000000000000001", 100.0, "current", ownerId, transactions);

        Account account = mapper.map(entity);

        assertThat(account.getIban()).isEqualTo("DE00000000000000000001");
        assertThat(account.getBalance()).isEqualTo(100.0);
        assertThat(account.getAccountType()).isEqualTo("current");
        assertThat(account.getOwnerId()).isEqualTo(ownerId);
        assertThat(account.getTransactions()).isEqualTo(transactions);
    }

    @Test
    void map_accountToEntity_withGivenIban_usesGivenIban() {
        UUID ownerId = UUID.randomUUID();
        Account account = new Account("DE00000000000000000002", 0.0, "savings", ownerId, List.of());

        AccountEntity entity = mapper.map("DE00000000000000000002", account);

        assertThat(entity.getIban()).isEqualTo("DE00000000000000000002");
        assertThat(entity.getAccountType()).isEqualTo("savings");
        assertThat(entity.getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    void map_accountToEntity_nullIban_resultsInNullEntityIban() {
        Account account = new Account(null, 0.0, "savings", UUID.randomUUID(), List.of());

        AccountEntity entity = mapper.map(null, account);

        assertThat(entity.getIban()).isNull();
    }
}
