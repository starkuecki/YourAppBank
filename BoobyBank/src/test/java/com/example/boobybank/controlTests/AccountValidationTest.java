package com.example.boobybank.controlTests;

import com.example.boobybank.control.accounts.Account;
import com.example.boobybank.control.shared.OnWrite;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountValidationTest {

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
    void onWrite_accountTypeNull_isConsideredValid() {
        Account account = new Account(null, null, null, UUID.randomUUID(), new ArrayList<>());

        Set<ConstraintViolation<Account>> violations = validator.validate(account, OnWrite.class);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("darf nicht null sein"));
    }

    @Test
    void onWrite_accountTypeInvalidValue_violatesPattern() {
        Account account = new Account("DE00000000000000000002", 100.0, "test", UUID.randomUUID(), new ArrayList<>());
        account.setAccountType("checking");

        Set<ConstraintViolation<Account>> violations = validator.validate(account, OnWrite.class);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("accountType"));
    }

    @Test
    void onWrite_OwnerIdNull_violatesNotNull() {
        Account account = new Account("DE00000000000000000002", 100.0, "savings", null, new ArrayList<>());

        Set<ConstraintViolation<Account>> violations = validator.validate(account, OnWrite.class);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ownerId"));
    }
}
