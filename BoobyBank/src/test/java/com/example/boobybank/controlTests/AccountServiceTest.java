package com.example.boobybank.controlTests;

import com.example.boobybank.control.accounts.*;
import com.example.boobybank.control.shared.NotFoundException;
import com.example.boobybank.entity.accounts.AccountEntity;
import com.example.boobybank.entity.accounts.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repo;

    private AccountMapper mapper;
    private AccountService service;

    private String iban;
    private UUID ownerId;
    private AccountEntity entity;

    @BeforeEach
    void setUp() {
        mapper = new AccountMapper();
        service = new AccountService(repo, mapper);
        iban = "DE00000000000000000001";
        ownerId = UUID.randomUUID();
        entity = new AccountEntity(iban, 100.0, "current", ownerId, new ArrayList<>());
    }

    @Test
    void getAccounts_withOwnerId_filtersByOwner() {
        when(repo.findByOwnerId(ownerId)).thenReturn(List.of(entity));

        var result = service.getAccounts(ownerId);

        assertThat(result).hasSize(1);
        verify(repo).findByOwnerId(ownerId);
        verify(repo, never()).findAll();
    }

    @Test
    void getAccounts_withoutOwnerId_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(entity));

        var result = service.getAccounts(null);

        assertThat(result).hasSize(1);
        verify(repo).findAll();
        verify(repo, never()).findByOwnerId(any());
    }

    @Test
    void getAccountById_existingIban_returnsMappedAccount() {
        when(repo.findById(iban)).thenReturn(Optional.of(entity));

        Account result = service.getAccountById(iban);

        assertThat(result.getIban()).isEqualTo(iban);
        assertThat(result.getBalance()).isEqualTo(100.0);
    }

    @Test
    void getAccountById_unknownIban_throwsNotFoundException() {
        when(repo.findById(iban)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAccountById(iban))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(iban);
    }

    @Test
    void getTransactions_delegatesToGetAccountById() {
        entity.getTransactions().add(new Deposit(10.0, "p", "t", "deposit"));
        when(repo.findById(iban)).thenReturn(Optional.of(entity));

        var result = service.getTransactions(iban);

        assertThat(result).hasSize(1);
    }

    @Test
    void createAccount_savesWithNullIban_returnsMappedAccount() {
        when(repo.save(any(AccountEntity.class))).thenReturn(entity);
        Account toCreate = new Account(null, 0.0, "current", ownerId, new ArrayList<>());
        toCreate.setAccountType("current");

        Account result = service.createAccount(toCreate);

        assertThat(result.getIban()).isEqualTo(iban); // aus dem gemockten save()-Ergebnis
        verify(repo).save(argThat(e -> e.getIban() == null));
    }

    @Test
    void updateAccount_existingIban_savesAndReturnsMappedAccount() {
        when(repo.existsById(iban)).thenReturn(true);
        when(repo.save(any(AccountEntity.class))).thenReturn(entity);
        Account toUpdate = new Account(iban, 0.0, "current", ownerId, new ArrayList<>());

        Account result = service.updateAccount(iban, toUpdate);

        assertThat(result.getIban()).isEqualTo(iban);
        verify(repo).save(argThat(e -> iban.equals(e.getIban())));
    }

    @Test
    void updateAccount_unknownIban_throwsNotFoundException_andNeverSaves() {
        when(repo.existsById(iban)).thenReturn(false);
        Account toUpdate = new Account(iban, 0.0, "current", ownerId, new ArrayList<>());

        assertThatThrownBy(() -> service.updateAccount(iban, toUpdate))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void deleteAccount_existingIban_deletesSuccessfully() {
        when(repo.existsById(iban)).thenReturn(true);

        service.deleteAccount(iban);

        verify(repo).deleteById(iban);
    }

    @Test
    void deleteAccount_unknownIban_throwsNotFoundException() {
        when(repo.existsById(iban)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteAccount(iban))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).deleteById(any());
    }

    @Test
    void deposit_existingAccount_increasesBalanceAndSaves() {
        when(repo.existsById(iban)).thenReturn(true);
        when(repo.findById(iban)).thenReturn(Optional.of(entity));
        Deposit deposit = new Deposit(50.0, "Gehalt", "2026-06-25T00:00:00.000Z", "deposit");

        Deposit result = service.deposit(iban, deposit);

        assertThat(result).isEqualTo(deposit);
        verify(repo).save(argThat(e -> e.getBalance() == 150.0));
    }

    @Test
    void deposit_unknownAccount_throwsNotFoundException_andNeverSaves() {
        when(repo.existsById(iban)).thenReturn(false);
        Deposit deposit = new Deposit(50.0, "Gehalt", "2026-06-25T00:00:00.000Z", "deposit");

        assertThatThrownBy(() -> service.deposit(iban, deposit))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void withdrawal_existingAccount_decreasesBalanceAndSaves() {
        when(repo.existsById(iban)).thenReturn(true);
        when(repo.findById(iban)).thenReturn(Optional.of(entity));
        Withdrawal withdrawal = new Withdrawal(30.0, "Miete", "2026-06-25T00:00:00.000Z", "withdrawal");

        Withdrawal result = service.withdrawal(iban, withdrawal);

        assertThat(result).isEqualTo(withdrawal);
        verify(repo).save(argThat(e -> e.getBalance() == 70.0));
    }

    @Test
    void withdrawal_unknownAccount_throwsNotFoundException_andNeverSaves() {
        when(repo.existsById(iban)).thenReturn(false);
        Withdrawal withdrawal = new Withdrawal(30.0, "Miete", "2026-06-25T00:00:00.000Z", "withdrawal");

        assertThatThrownBy(() -> service.withdrawal(iban, withdrawal))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).save(any());
    }
}
