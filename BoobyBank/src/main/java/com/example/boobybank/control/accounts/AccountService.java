package com.example.boobybank.control.accounts;

import com.example.boobybank.control.shared.NotFoundException;
import com.example.boobybank.control.shared.OnWrite;
import com.example.boobybank.entity.accounts.AccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repo;
    private final AccountMapper mapper;

    public Collection<Account> getAccounts(UUID ownerId) {
        if (ownerId != null){
            return repo.findByOwnerId(ownerId)
                    .stream()
                    .map(mapper::map)
                    .toList();
        }
        return repo.findAll()
                .stream()
                .map(mapper::map)
                .toList();
    }

    public Account getAccountById(String iban) {
        return repo.findById(iban)
                .map(mapper::map)
                .orElseThrow(() -> new NotFoundException("Account with iban " + iban + " does not exist"));
    }

    public Collection<Transaction> getTransactions(String iban) {
        return getAccountById(iban).getTransactions();
    }

    @Validated(OnWrite.class)
    public Account createAccount(@Valid Account account) {
        return mapper.map(repo.save(mapper.map(null, account)));
    }

    @Validated(OnWrite.class)
    public Account updateAccount(String iban, @Valid Account account) {
        requireAccountExists(iban);

        return mapper.map(repo.save(mapper.map(iban, account)));
    }

    public void deleteAccount(String iban) {
        requireAccountExists(iban);
        repo.deleteById(iban);
    }

    private void requireAccountExists(String iban) {
        if (!repo.existsById(iban)) {
            throw new NotFoundException("Account with iban " + iban + " does not exist");
        }
    }

    public Deposit deposit(String iban, Deposit deposit) {
        requireAccountExists(iban);
        Account account = getAccountById(iban);
        account.addTransaction(deposit);
        repo.save(mapper.map(iban, account));
        return deposit;
    }

    public Withdrawal withdrawal(String iban, Withdrawal withdrawal) {
        requireAccountExists(iban);
        Account account = getAccountById(iban);
        account.addTransaction(withdrawal);
        repo.save(mapper.map(iban, account));
        return withdrawal;
    }

}
