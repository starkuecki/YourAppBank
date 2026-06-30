package com.example.boobybank.boundaryTests;

import com.example.boobybank.boundary.shared.GlobalExceptionHandler;
import com.example.boobybank.control.customers.Customer;
import com.example.boobybank.control.shared.NotFoundException;
import com.example.boobybank.control.shared.UnknownCustomerException;
import com.example.boobybank.boundary.shared.ConstraintViolationDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundException_returns404WithMessage() {
        NotFoundException ex = new NotFoundException("Customer with id xyz does not exist");

        ProblemDetail result = handler.handleNotFoundException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).isEqualTo("Customer with id xyz does not exist");
    }

    @Test
    void handleUnknownCustomerException_returns422WithMessage() {
        UnknownCustomerException ex = new UnknownCustomerException("Customer unknown");

        ProblemDetail result = handler.handleNotFoundException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
        assertThat(result.getTitle()).isEqualTo("Unknown Customer");
        assertThat(result.getDetail()).isEqualTo("Customer unknown");
    }

    @Test
    void handleIllegalArgumentException_returns400WithMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid UUID string: abc");

        ProblemDetail result = handler.handleIllegalArgumentException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getDetail()).contains("Invalid UUID string: abc");
    }

    @Test
    void handleConstraintViolationException_returns422WithViolationsList() {
        var validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        Customer invalidCustomer = new Customer(null, "Hamburg"); // name verletzt @NotNull
        invalidCustomer.setPassword("pw");

        Set<ConstraintViolation<Customer>> rawViolations = validator.validate(invalidCustomer);
        ConstraintViolationException ex = new ConstraintViolationException(rawViolations);

        ProblemDetail result = handler.handleConstraintViolationException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(result.getTitle()).isEqualTo("Unprocessable Content");
        assertThat(result.getProperties()).containsKey("violations");

        @SuppressWarnings("unchecked")
        var violations = (java.util.List<ConstraintViolationDTO>) result.getProperties().get("violations");
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getField()).isEqualTo("name");
    }

}

