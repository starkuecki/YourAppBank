package com.example.boobybank.entityTests;

import com.example.boobybank.entity.accounts.AccountEntity;
import com.example.boobybank.entity.accounts.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findByOwnerId_returnsAllAccountsOfOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID otherOwnerId = UUID.randomUUID();

        accountRepository.save(new AccountEntity("DE00000000000000000001", 100.0, "current", ownerId, List.of()));
        accountRepository.save(new AccountEntity("DE00000000000000000002", 200.0, "savings", ownerId, List.of()));
        accountRepository.save(new AccountEntity("DE00000000000000000003", 300.0, "current", otherOwnerId, List.of()));

        Collection<AccountEntity> result = accountRepository.findByOwnerId(ownerId);

        assertThat(result)
                .hasSize(2)
                .extracting(AccountEntity::getIban)
                .containsExactlyInAnyOrder("DE00000000000000000001", "DE00000000000000000002");
    }

    @Test
    void findByOwnerId_noAccounts_returnsEmptyCollection() {
        Collection<AccountEntity> result = accountRepository.findByOwnerId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}