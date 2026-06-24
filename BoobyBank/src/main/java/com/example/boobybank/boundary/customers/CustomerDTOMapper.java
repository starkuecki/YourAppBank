package com.example.boobybank.boundary.customers;

import com.example.boobybank.control.customers.Customer;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerDTOMapper {

    public CustomerDTO map(Customer customer) {
        return new CustomerDTO(
                customer.getId().toString(),
                customer.getName(),
                customer.getCity()
        );
    }


    public Customer map(String id, @NonNull CustomerDTO customerDTO) {
        return new Customer(
                id == null ? null : UUID.fromString(id),
                customerDTO.getName(),
                customerDTO.getCity()
        );
    }

}