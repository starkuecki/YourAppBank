package com.example.boobybank.entityTests;

import com.example.boobybank.entity.accounts.AccountEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountEntityTest {

    private AccountEntity newAccount(String iban) {
        return new AccountEntity(iban, 100.0, "current", UUID.randomUUID(), List.of());
    }

    @Test
    void equals_sameIban_isEqual() {
        AccountEntity a = newAccount("DE00000000000000000001");
        AccountEntity b = newAccount("DE00000000000000000001");
        b.setBalance(999.0); // unterschiedliche andere Felder, IBAN entscheidet

        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_differentIban_isNotEqual() {
        AccountEntity a = newAccount("DE00000000000000000001");
        AccountEntity b = newAccount("DE00000000000000000002");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_nullIban_isNotEqual() {
        AccountEntity a = newAccount(null);
        AccountEntity b = newAccount(null);

        // getIban() != null ist Teil der equals-Bedingung -> zwei "leere" Accounts sind NICHT gleich
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_null_isFalse() {
        AccountEntity a = newAccount("DE00000000000000000001");
        assertThat(a.equals(null)).isFalse();
    }

    @Test
    void equals_differentType_isFalse() {
        AccountEntity a = newAccount("DE00000000000000000001");
        assertThat(a.equals("not an account")).isFalse();
    }

    @Test
    void hashCode_isConstantForClass_notDependentOnFields() {
        // Bewusstes Hibernate-Proxy-sicheres Pattern: hashCode() hängt NICHT vom Feldwert ab.
        AccountEntity a = newAccount("DE00000000000000000001");
        AccountEntity b = newAccount("DE00000000000000000002");

        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
