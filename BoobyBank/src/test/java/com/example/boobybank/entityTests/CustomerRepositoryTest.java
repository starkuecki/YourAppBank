package com.example.boobybank.entityTests;

import com.example.boobybank.entity.customer.CustomerEntity;
import com.example.boobybank.entity.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findByNameContainingIgnoreCase_matchesCaseInsensitive() {
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Max Mustermann", "Hamburg", "pw"));
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Erika Musterfrau", "Berlin", "pw"));

        Collection<CustomerEntity> result = customerRepository.findByNameContainingIgnoreCase("muster");

        assertThat(result).hasSize(2);
    }

    @Test
    void findByCityContainingIgnoreCase_matchesCaseInsensitive() {
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Max Mustermann", "Hamburg", "pw"));
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Erika Musterfrau", "Berlin", "pw"));

        Collection<CustomerEntity> result = customerRepository.findByCityContainingIgnoreCase("ham");

        assertThat(result)
                .hasSize(1)
                .extracting(CustomerEntity::getName)
                .containsExactly("Max Mustermann");
    }

    @Test
    void findByNameAndCityContainingIgnoreCase_combinesBothFilters() {
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Max Mustermann", "Hamburg", "pw"));
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Max Müller", "Berlin", "pw"));

        Collection<CustomerEntity> result =
                customerRepository.findByNameContainingIgnoreCaseAndCityContainingIgnoreCase("max", "hamburg");

        assertThat(result)
                .hasSize(1)
                .extracting(CustomerEntity::getId)
                .isNotEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        customerRepository.save(new CustomerEntity(UUID.randomUUID(), "Max Mustermann", "Hamburg", "pw"));

        Collection<CustomerEntity> result = customerRepository.findByNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }
}