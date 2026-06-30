package com.example.boobybank.boundaryTests;

import com.example.boobybank.boundary.accounts.AccountDTO;
import com.example.boobybank.boundary.accounts.AccountDTOMapper;
import com.example.boobybank.boundary.accounts.DepositDTO;
import com.example.boobybank.boundary.accounts.WithdrawalDTO;
import com.example.boobybank.control.accounts.Account;
import com.example.boobybank.control.accounts.Deposit;
import com.example.boobybank.control.accounts.Withdrawal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountDTOMapperTest {

    private final AccountDTOMapper mapper = new AccountDTOMapper();

    @Test
    void map_accountToDto_copiesAllFields() {
        UUID ownerId = UUID.randomUUID();
        Account account = new Account("DE00000000000000000001", 150.0, "current", ownerId, new ArrayList<>());

        AccountDTO dto = mapper.map(account);

        assertThat(dto.getIban()).isEqualTo("DE00000000000000000001");
        assertThat(dto.getBalance()).isEqualTo(150.0);
        assertThat(dto.getAccountType()).isEqualTo("current");
        assertThat(dto.getOwnerId()).isEqualTo(ownerId.toString());
    }

    @Test
    void map_dtoToAccount_setsBalanceToZero_regardlessOfDtoValue() {
        // Dokumentiert bewusstes Verhalten: balance wird beim Erstellen server-seitig auf 0 gesetzt,
        // unabhängig vom (eigentlich READ_ONLY) Wert im DTO.
        UUID ownerId = UUID.randomUUID();
        AccountDTO dto = new AccountDTO("DE00000000000000000001", 9999.0, "current", ownerId.toString());

        Account account = mapper.map(dto);

        assertThat(account.getBalance()).isEqualTo(0.0);
    }

    @Test
    void map_dtoToAccount_parsesOwnerIdAndCopiesOtherFields() {
        UUID ownerId = UUID.randomUUID();
        AccountDTO dto = new AccountDTO("DE00000000000000000001", null, "savings", ownerId.toString());

        Account account = mapper.map(dto);

        assertThat(account.getIban()).isEqualTo("DE00000000000000000001");
        assertThat(account.getAccountType()).isEqualTo("savings");
        assertThat(account.getOwnerId()).isEqualTo(ownerId);
        assertThat(account.getTransactions()).isEmpty();
    }

    @Test
    void map_dtoToAccount_invalidOwnerIdFormat_throwsIllegalArgumentException() {
        // Dokumentiert die ungefangene UUID.fromString()-Exception (wird vom GlobalExceptionHandler abgefangen).
        AccountDTO dto = new AccountDTO("DE00000000000000000001", null, "current", "not-a-uuid");

        assertThatThrownBy(() -> mapper.map(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void map_depositDtoToDeposit_setsTransactionTypeToDeposit() {
        DepositDTO dto = new DepositDTO(50.0, "Gehalt", "2026-06-25T00:00:00.000Z");

        Deposit deposit = mapper.map(dto);

        assertThat(deposit.getAmount()).isEqualTo(50.0);
        assertThat(deposit.getPurpose()).isEqualTo("Gehalt");
        assertThat(deposit.getTimestamp()).isEqualTo("2026-06-25T00:00:00.000Z");
        assertThat(deposit.getTransactionType()).isEqualTo("deposit");
    }

    @Test
    void map_depositToDepositDto_copiesFields() {
        Deposit deposit = new Deposit(50.0, "Gehalt", "2026-06-25T00:00:00.000Z", "deposit");

        DepositDTO dto = mapper.map(deposit);

        assertThat(dto.getAmount()).isEqualTo(50.0);
        assertThat(dto.getPurpose()).isEqualTo("Gehalt");
        assertThat(dto.getTimestamp()).isEqualTo("2026-06-25T00:00:00.000Z");
    }

    @Test
    void map_withdrawalDtoToWithdrawal_setsTransactionTypeToWithdrawal() {
        WithdrawalDTO dto = new WithdrawalDTO(30.0, "Miete", "2026-06-25T00:00:00.000Z");

        Withdrawal withdrawal = mapper.map(dto);

        assertThat(withdrawal.getTransactionType()).isEqualTo("withdrawal");
    }

    @Test
    void map_withdrawalToWithdrawalDto_copiesFields() {
        Withdrawal withdrawal = new Withdrawal(30.0, "Miete", "2026-06-25T00:00:00.000Z", "withdrawal");

        WithdrawalDTO dto = mapper.map(withdrawal);

        assertThat(dto.getAmount()).isEqualTo(30.0);
        assertThat(dto.getPurpose()).isEqualTo("Miete");
        assertThat(dto.getTimestamp()).isEqualTo("2026-06-25T00:00:00.000Z");
    }
}
