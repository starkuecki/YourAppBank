package com.example.boobybank.controlTests;

import com.example.boobybank.control.customers.Customer;
import com.example.boobybank.control.shared.OnRead;
import com.example.boobybank.control.shared.OnWrite;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void onWrite_idMustBeNull_setIdPresent_violates() {
        Customer customer = new Customer("Max", "Hamburg");
        customer.setPassword("pw");
        customer.setId(UUID.randomUUID());

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer, OnWrite.class);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("id"));
    }

    @Test
    void onWrite_idNull_isValid() {
        Customer customer = new Customer("Max", "Hamburg");
        customer.setPassword("pw");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer, OnWrite.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void onRead_idMustBePresent_idNull_violates() {
        Customer customer = new Customer("Max", "Hamburg");
        customer.setPassword("pw");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer, OnRead.class);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("id"));
    }
}
