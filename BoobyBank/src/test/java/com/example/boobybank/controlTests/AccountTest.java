package com.example.boobybank.controlTests;

import com.example.boobybank.control.accounts.Account;
import com.example.boobybank.control.accounts.Deposit;
import com.example.boobybank.control.accounts.Withdrawal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    private final Account accountTest = new Account("DE00000000000000000002", 100.0, "savings", UUID.randomUUID(), new ArrayList<>());

    @Test
    void addTransaction_deposit_increasesBalance() {
        Deposit deposit = new Deposit(50.0, "Gehalt", "2026-06-25T00:00:00.000Z", "deposit");

        accountTest.addTransaction(new Deposit(50.0, "Gehalt", "2026-06-25T00:00:00.000Z", "deposit"));

        assertThat(accountTest.getBalance()).isEqualTo(150.0);
        assertThat(accountTest.getTransactions()).containsExactly(deposit);
    }

    @Test
    void addTransaction_withdrawal_decreasesBalance() {
        Withdrawal withdrawal = new Withdrawal(30.0, "Miete", "2026-06-25T00:00:00.000Z", "withdrawal");

        accountTest.addTransaction(withdrawal);

        assertThat(accountTest.getBalance()).isEqualTo(70.0);
        assertThat(accountTest.getTransactions()).containsExactly(withdrawal);
    }

    @Test
    void addTransaction_withdrawalExceedingBalance_stillExecutes_resultsInNegativeBalance() {
        Withdrawal withdrawal = new Withdrawal(150.0, "zu viel", "2026-06-25T00:00:00.000Z", "withdrawal");

        accountTest.addTransaction(withdrawal);

        assertThat(accountTest.getBalance()).isEqualTo(-50.0);
    }

    @Test
    void addTransaction_multipleTransactions_accumulateCorrectly() {

        accountTest.addTransaction(new Deposit(100.0, "p1", "t1", "deposit"));
        accountTest.addTransaction(new Withdrawal(10.0, "p2", "t2", "withdrawal"));
        accountTest.addTransaction(new Deposit(50.0, "p3", "t3", "deposit"));
        accountTest.addTransaction(new Withdrawal(30.0, "p4", "t4", "withdrawal"));

        assertThat(accountTest.getBalance()).isEqualTo(210.0);
        assertThat(accountTest.getTransactions()).hasSize(4);
    }
}
