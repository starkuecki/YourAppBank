package com.example.boobybank.boundaryTests;

import com.example.boobybank.boundary.customers.CustomerDTO;
import com.example.boobybank.boundary.customers.CustomerDTOMapper;
import com.example.boobybank.control.customers.Customer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerDTOMapperTest {

    private final CustomerDTOMapper mapper = new CustomerDTOMapper();

    @Test
    void map_customerToDto_copiesAllFields() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer("Max Mustermann", "Hamburg");
        customer.setId(id);
        customer.setPassword("hashed-pw");

        CustomerDTO dto = mapper.map(customer);

        assertThat(dto.getId()).isEqualTo(id.toString());
        assertThat(dto.getName()).isEqualTo("Max Mustermann");
        assertThat(dto.getCity()).isEqualTo("Hamburg");
        assertThat(dto.getPassword()).isEqualTo("hashed-pw");
    }

    @Test
    void map_dtoToCustomer_withGivenId_parsesId() {
        UUID id = UUID.randomUUID();
        CustomerDTO dto = new CustomerDTO(null, "Max Mustermann", "Hamburg", "pw");

        Customer customer = mapper.map(id.toString(), dto);

        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getName()).isEqualTo("Max Mustermann");
        assertThat(customer.getCity()).isEqualTo("Hamburg");
        assertThat(customer.getPassword()).isEqualTo("pw");
    }

    @Test
    void map_dtoToCustomer_nullId_resultsInNullCustomerId() {
        CustomerDTO dto = new CustomerDTO(null, "Max Mustermann", "Hamburg", "pw");

        Customer customer = mapper.map(null, dto);

        assertThat(customer.getId()).isNull();
    }

    @Test
    void map_dtoToCustomer_invalidIdFormat_throwsIllegalArgumentException() {
        CustomerDTO dto = new CustomerDTO(null, "Max Mustermann", "Hamburg", "pw");

        assertThatThrownBy(() -> mapper.map("not-a-uuid", dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
