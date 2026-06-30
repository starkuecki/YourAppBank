package com.example.boobybank.controlTests;

import com.example.boobybank.control.customers.Customer;
import com.example.boobybank.control.customers.CustomerMapper;
import com.example.boobybank.entity.customer.CustomerEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void map_entityToCustomer_copiesAllFields() {
        UUID id = UUID.randomUUID();
        CustomerEntity entity = new CustomerEntity(id, "Max", "Hamburg", "hashed-pw");

        Customer customer = mapper.map(entity);

        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getName()).isEqualTo("Max");
        assertThat(customer.getCity()).isEqualTo("Hamburg");
        assertThat(customer.getPassword()).isEqualTo("hashed-pw");
    }

    @Test
    void map_customerToEntity_withGivenId_usesGivenId() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer("Max", "Hamburg");
        customer.setPassword("hashed-pw");

        CustomerEntity entity = mapper.map(id, customer);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Max");
        assertThat(entity.getCity()).isEqualTo("Hamburg");
        assertThat(entity.getPassword()).isEqualTo("hashed-pw");
    }

    @Test
    void map_customerToEntity_nullId_resultsInNullEntityId() {
        Customer customer = new Customer("Max", "Hamburg");
        customer.setPassword("hashed-pw");

        CustomerEntity entity = mapper.map(null, customer);

        assertThat(entity.getId()).isNull();
    }
}
