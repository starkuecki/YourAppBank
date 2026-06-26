package com.example.boobybank.control.customers;

import com.example.boobybank.entity.customer.CustomerEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMapper {

    @NonNull
    public Customer map(@NonNull CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getName(),
                entity.getCity(),
                entity.getPassword()
        );
    }


    @NonNull
    public CustomerEntity map(UUID id, @NonNull Customer customer) {
        return new CustomerEntity(
                id,
                customer.getName(),
                customer.getCity(),
                customer.getPassword()
        );
    }

}