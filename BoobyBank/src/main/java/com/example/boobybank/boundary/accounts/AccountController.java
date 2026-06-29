package com.example.boobybank.boundary.accounts;

import com.example.boobybank.boundary.customers.CustomerDTO;
import com.example.boobybank.control.accounts.*;
import com.example.boobybank.control.customers.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final AccountDTOMapper mapper;


    @GetMapping
    public Collection<AccountDTO> getAccounts(
            @RequestParam(required = false) String ownerId
    ) {

        UUID ownerUuid = (ownerId != null) ? UUID.fromString(ownerId) : null;

        return accountService.getAccounts(ownerUuid)
                .stream()
                .map(mapper::map)
                .toList();
    }

    @GetMapping("/{iban}")
    public AccountDTO getAccount(
            @PathVariable String iban
    ) {

        return mapper.map(accountService.getAccountById(iban));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @RequestBody @Valid AccountDTO account,
            UriComponentsBuilder uriBuilder
    ) {
        Account newAccount = accountService
                .createAccount(mapper.map(account));
        String iban = newAccount.getIban();

        URI location = uriBuilder
                .path("/{iban}")
                .buildAndExpand(iban)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(mapper.map(newAccount));
    }

    @PostMapping("/{iban}/deposit")
    public ResponseEntity<DepositDTO> deposit(
            @RequestBody @Valid DepositDTO deposit,
            @PathVariable String iban,
            UriComponentsBuilder uriBuilder
    ) {
        Deposit newDeposit = accountService
                .deposit(iban, mapper.map(deposit));

        URI location = uriBuilder
                .path("/{iban}")
                .buildAndExpand(iban)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(mapper.map(newDeposit));
    }

    @PostMapping("/{iban}/withdrawal")
    public ResponseEntity<WithdrawalDTO> withdrawal(
            @RequestBody @Valid WithdrawalDTO withdrawal,
            @PathVariable String iban,
            UriComponentsBuilder uriBuilder
    ) {
        Withdrawal newWithdrawal = accountService
                .withdrawal(iban, mapper.map(withdrawal));

        URI location = uriBuilder
                .path("/{iban}")
                .buildAndExpand(iban)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(mapper.map(newWithdrawal));
    }

    @PutMapping("/{iban}")
    public AccountDTO updateAccount(
            @PathVariable String iban,
            @RequestBody @Valid AccountDTO account
    ) {
        Account updatedAccount = accountService.updateAccount(
                iban,
                mapper.map(account)
        );

        return mapper.map(updatedAccount);
    }

    @DeleteMapping("/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable String iban) {
        accountService.deleteAccount(iban);
    }



    @GetMapping("/{iban}/transactions")
    public Collection<Transaction> getTransactions(
            @PathVariable String iban
    ){
        return accountService.getTransactions(iban);
    }

}

