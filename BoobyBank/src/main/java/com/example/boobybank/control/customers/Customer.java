package com.example.boobybank.control.customers;

import com.example.boobybank.control.shared.OnRead;
import com.example.boobybank.control.shared.OnWrite;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Customer {

    @Null(groups = OnWrite.class)
    @NotNull(groups = OnRead.class)
    private UUID id;

    @NotNull
    @Size(min = 2, max = 100)
    private final String name;

    @NotNull
    @Size(min = 2, max = 100)
    private final String city;

}