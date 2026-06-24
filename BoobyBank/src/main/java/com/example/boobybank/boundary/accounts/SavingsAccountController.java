package com.example.boobybank.boundary.accounts;

import com.example.boobybank.control.shared.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/savings-accounts")
public class SavingsAccountController {

    private final Map<String, SavingsAccountDTO> savingsAccounts = new HashMap<>();


    public SavingsAccountController() {
        String iban1 = "DE12545678";
        SavingsAccountDTO account1 = new SavingsAccountDTO(iban1, 100.0, 0.05f);

        String iban2 = "DE87554321";
        SavingsAccountDTO account2 = new SavingsAccountDTO(iban2, 200.0, 0.06f);

        savingsAccounts.put(iban1, account1);
        savingsAccounts.put(iban2, account2);
    }


    @GetMapping
    public Collection<SavingsAccountDTO> getSavingsAccounts() {
        return savingsAccounts.values();
    }


    @PostMapping
    public ResponseEntity<SavingsAccountDTO> createAccount(
            @RequestBody @Valid SavingsAccountDTO account,
            UriComponentsBuilder uriBuilder
    ) {
        if (savingsAccounts.containsKey(account.getIban())) {
            return ResponseEntity.badRequest().build();
        }

        if (!account.getAccountType().equals("savings")) {
            return ResponseEntity.badRequest().build();
        }

        var iban = account.getIban();
        var newAccount = SavingsAccountDTO.newFrom(account);

        savingsAccounts.put(iban, newAccount);
        var location = uriBuilder
                .path("/{iban}")
                .buildAndExpand(iban)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(newAccount);
    }


    @GetMapping("/{iban}")
    public ResponseEntity<SavingsAccountDTO> getSavingsAccount(
            @PathVariable String iban
    ) {
        requireAccountExists(iban);

        return ResponseEntity.ok(savingsAccounts.get(iban));
    }


    @PutMapping("/{iban}")
    public ResponseEntity<SavingsAccountDTO> updateAccount(
            @PathVariable String iban,
            @RequestBody @Valid SavingsAccountDTO account
    ) {
        requireAccountExists(iban);

        var copiedAccount = SavingsAccountDTO.copyFrom(account);

        savingsAccounts.put(iban, copiedAccount);

        return ResponseEntity.ok(copiedAccount);
    }


    @DeleteMapping("/{iban}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String iban
    ) {
        requireAccountExists(iban);

        savingsAccounts.remove(iban);

        return ResponseEntity.noContent().build();
    }


    private void requireAccountExists(String iban) {
        if (!savingsAccounts.containsKey(iban)) {
            throw new NotFoundException("Account with IBAN " + iban + " does not exist");
        }
    }

}
