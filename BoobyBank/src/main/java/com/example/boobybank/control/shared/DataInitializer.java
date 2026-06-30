package com.example.boobybank.control.shared;

import com.example.boobybank.control.accounts.AccountService;
import com.example.boobybank.control.accounts.Deposit;
import com.example.boobybank.control.accounts.Withdrawal;
import com.example.boobybank.control.customers.CustomerService;
import com.example.boobybank.entity.accounts.AccountEntity;
import com.example.boobybank.entity.accounts.AccountRepository;
import com.example.boobybank.entity.customer.CustomerEntity;
import com.example.boobybank.entity.customer.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CustomerService customersService;
    private final CustomerRepository cRepo;
    private final AccountService accountService;
    private final AccountRepository aRepo;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (customersService.getCustomerCount() > 0) {
            return;
        }

        HashUtil hasher = new HashUtil();

        CustomerEntity customerEntity1 = new CustomerEntity(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Tim",
                "Beispielstadt1",
                hasher.sha256("1")
        );
        cRepo.save(customerEntity1);

        AccountEntity accountEntity1 = new AccountEntity(
                "DE10000000000000000001",
                0,
                "current",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity1);

        accountService.deposit(
                "DE10000000000000000001",
                new Deposit(
                        100.0,
                        "Einzahlungsbeispiel1",
                        "2026-06-25T23:33:00.000Z",
                        "deposit"
                )
        );

        accountService.withdrawal(
                "DE10000000000000000001",
                new Withdrawal(
                        10.0,
                        "Auszahlungsbeispiel1",
                        "2026-06-25T23:34:00.000Z",
                        "withdrawal"
                )
        );

        accountService.deposit(
                "DE10000000000000000001",
                new Deposit(
                        50.0,
                        "Einzahlungsbeispiel2",
                        "2026-06-25T23:35:00.000Z",
                        "deposit"
                )
        );

        accountService.withdrawal(
                "DE10000000000000000001",
                new Withdrawal(
                        30.0,
                        "Auszahlungsbeispiel2",
                        "2026-06-25T23:36:00.000Z",
                        "withdrawal"
                )
        );

        AccountEntity accountEntity2 = new AccountEntity(
                "DE10000000000000000002",
                0,
                "savings",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity2);






        CustomerEntity customerEntity2 = new CustomerEntity(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Tom",
                "Beispielstadt2",
                hasher.sha256("2")
        );
        cRepo.save(customerEntity2);

        AccountEntity accountEntity3 = new AccountEntity(
                "DE20000000000000000001",
                0,
                "current",
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity3);

        accountService.deposit(
                "DE20000000000000000001",
                new Deposit(
                        12.0,
                        "Einzahlungsbeispiel1",
                        "2026-06-25T23:33:00.000Z",
                        "deposit"
                )
        );

        accountService.withdrawal(
                "DE20000000000000000001",
                new Withdrawal(
                        1.0,
                        "Auszahlungsbeispiel1",
                        "2026-06-25T23:34:00.000Z",
                        "withdrawal"
                )
        );

        accountService.deposit(
                "DE20000000000000000001",
                new Deposit(
                        3.0,
                        "Einzahlungsbeispiel2",
                        "2026-06-25T23:35:00.000Z",
                        "deposit"
                )
        );

        accountService.withdrawal(
                "DE20000000000000000001",
                new Withdrawal(
                        2.0,
                        "Auszahlungsbeispiel2",
                        "2026-06-25T23:36:00.000Z",
                        "withdrawal"
                )
        );

        AccountEntity accountEntity4 = new AccountEntity(
                "DE20000000000000000002",
                0,
                "savings",
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity4);

    }

}