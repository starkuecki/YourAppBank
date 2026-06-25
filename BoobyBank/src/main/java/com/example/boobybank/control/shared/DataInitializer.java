package com.example.boobybank.control.shared;

import com.example.boobybank.control.customers.Customer;
import com.example.boobybank.control.customers.CustomerService;
import com.example.boobybank.entity.accounts.AccountEntity;
import com.example.boobybank.entity.accounts.AccountRepository;
import com.example.boobybank.entity.customer.CustomerEntity;
import com.example.boobybank.entity.customer.CustomerRepository;
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
    private final AccountRepository aRepo;


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


        CustomerEntity customerEntity1 = new CustomerEntity(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Maximilian",
                "Beispielstadt"
        );
        cRepo.save(customerEntity1);

        AccountEntity accountEntity1 = new AccountEntity(
                "DE11111111111111111111",
                0,
                "current",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new ArrayList<>()
        );
        aRepo.save(accountEntity1);

    }

}