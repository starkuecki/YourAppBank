package com.example.boobybank.control.shared;

public class UnknownCustomerException extends RuntimeException {
    public UnknownCustomerException(String message) {
        super(message);
    }
}
