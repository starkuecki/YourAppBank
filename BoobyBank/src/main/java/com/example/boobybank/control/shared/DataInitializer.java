package com.example.boobybank.control.shared;

import com.example.boobybank.control.accounts.AccountService;
import com.example.boobybank.control.accounts.Deposit;
import com.example.boobybank.control.accounts.Withdrawal;
import com.example.boobybank.control.customers.Customer;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
/*
        log.info("Initializing customers...");
        Customer customer1 = new Customer("Max Musterfrau", "Hamburg");
        Customer customer2 = new Customer("Peter Mustermann", "Berlin");

        customersService.createCustomer(customer1);
        customersService.createCustomer(customer2);
        log.info("Customers initialized");*/

        String hash = "";
        try {
            String password = "1";

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Bytes in lesbaren Hex-String umwandeln
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
                hash = hexString.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }



        CustomerEntity customerEntity1 = new CustomerEntity(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Max",
                "Beispielstadt",
                hash
        );
        cRepo.save(customerEntity1);

        AccountEntity accountEntity1 = new AccountEntity(
                "DE00000000000000000001",
                0,
                "current",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity1);

        accountService.deposit(
                "DE00000000000000000001",
                new Deposit(
                        100.0,
                        "Einzahlungsbeispiel1",
                        "2026-06-25T23:33:00.000Z",
                        "deposit"
                )
        );

        accountService.withdrawal(
                "DE00000000000000000001",
                new Withdrawal(
                        10.0,
                        "Auszahlungsbeispiel1",
                        "2026-06-25T23:34:00.000Z",
                        "withdrawal"
                )
        );

        accountService.deposit(
                "DE00000000000000000001",
                new Deposit(
                        50.0,
                        "Einzahlungsbeispiel2",
                        "2026-06-25T23:35:00.000Z",
                        "deposit"
                )
        );

        accountService.withdrawal(
                "DE00000000000000000001",
                new Withdrawal(
                        30.0,
                        "Auszahlungsbeispiel2",
                        "2026-06-25T23:36:00.000Z",
                        "withdrawal"
                )
        );

        AccountEntity accountEntity2 = new AccountEntity(
                "DE00000000000000000002",
                0,
                "savings",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity2);

    }

}