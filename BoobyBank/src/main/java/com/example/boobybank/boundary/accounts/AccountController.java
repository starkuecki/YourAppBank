package com.example.boobybank.boundary.accounts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("v1/accounts")
public class AccountController {

    private final Map<String, AccountDTO> accounts = new HashMap<>();


    public AccountController() {
        String iban1 = "DE12345678";
        String iban2 = "DE87654321";
        accounts.put(iban1, new AccountDTO(iban1, 234.56, "current"));
        accounts.put(iban2, new AccountDTO(iban2, 100.0, "savings"));
    }

    @GetMapping
    public Collection<AccountDTO> getAccounts() {
        return accounts.values();
    }

    @GetMapping("/{iban}")
    public ResponseEntity<AccountDTO> getAccount(
            @PathVariable String iban
    ) {
        if (!accounts.containsKey(iban)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(accounts.get(iban));
    }

    @DeleteMapping("/{iban}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String iban
    ) {
        if (!accounts.containsKey(iban)) {
            return ResponseEntity.notFound().build();
        }

        accounts.remove(iban);

        return ResponseEntity.noContent().build();
    }

}

