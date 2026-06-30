package com.example.boobybank.entityTests;

import com.example.boobybank.entity.customer.CustomerEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerEntityTest {

    private CustomerEntity newCustomer(UUID id) {
        return new CustomerEntity(id, "Max Mustermann", "Hamburg", "hashed-pw");
    }

    @Test
    void equals_sameId_isEqual() {
        UUID id = UUID.randomUUID();
        CustomerEntity a = newCustomer(id);
        CustomerEntity b = newCustomer(id);
        b.setCity("Berlin");

        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_differentId_isNotEqual() {
        CustomerEntity a = newCustomer(UUID.randomUUID());
        CustomerEntity b = newCustomer(UUID.randomUUID());

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_nullId_isNotEqual() {
        CustomerEntity a = newCustomer(null);
        CustomerEntity b = newCustomer(null);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_null_isFalse() {
        assertThat(newCustomer(UUID.randomUUID()).equals(null)).isFalse();
    }

    @Test
    void hashCode_isConstantForClass_notDependentOnFields() {
        CustomerEntity a = newCustomer(UUID.randomUUID());
        CustomerEntity b = newCustomer(UUID.randomUUID());

        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}