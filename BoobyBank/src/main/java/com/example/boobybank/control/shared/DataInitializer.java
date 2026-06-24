package com.example.boobybank.control.shared;

import com.example.boobybank.control.customers.Customer;
import com.example.boobybank.control.customers.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CustomerService customersService;


    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (customersService.getCustomerCount() > 0) {
            return;
        }

        log.info("Initializing customers...");
        Customer customer1 = new Customer("Max Musterfrau", "Hamburg");
        Customer customer2 = new Customer("Peter Mustermann", "Berlin");

        customersService.createCustomer(customer1);
        customersService.createCustomer(customer2);
        log.info("Customers initialized");
    }

}