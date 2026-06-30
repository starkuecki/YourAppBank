package com.example.boobybank.controlTests;

import com.example.boobybank.control.customers.CustomerMapper;
import com.example.boobybank.control.customers.CustomerService;
import com.example.boobybank.control.customers.Customer;
import com.example.boobybank.control.shared.NotFoundException;
import com.example.boobybank.entity.customer.CustomerEntity;
import com.example.boobybank.entity.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repo;

    private CustomerMapper mapper; // bewusst real, da reine Mapping-Logik ohne externe Abhängigkeiten

    private CustomerService service;

    private UUID customerId;
    private CustomerEntity entity;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMapper();
        service = new CustomerService(repo, mapper);
        customerId = UUID.randomUUID();
        entity = new CustomerEntity(customerId, "Max Mustermann", "Hamburg", "hashed-pw");
    }

    @Test
    void getCustomers_noFilters_returnsAllCustomers() {
        when(repo.findAll()).thenReturn(List.of(entity));

        var result = service.getCustomers(null, null);

        assertThat(result).hasSize(1);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getCustomers_nameOnly_usesNameFilter() {
        when(repo.findByNameContainingIgnoreCase("max")).thenReturn(List.of(entity));

        var result = service.getCustomers("max", null);

        assertThat(result).hasSize(1);
        verify(repo).findByNameContainingIgnoreCase("max");
        verify(repo, never()).findAll();
    }

    @Test
    void getCustomers_cityOnly_usesCityFilter() {
        when(repo.findByCityContainingIgnoreCase("hamburg")).thenReturn(List.of(entity));

        var result = service.getCustomers(null, "hamburg");

        assertThat(result).hasSize(1);
        verify(repo).findByCityContainingIgnoreCase("hamburg");
    }

    @Test
    void getCustomers_nameAndCity_usesCombinedFilter() {
        when(repo.findByNameContainingIgnoreCaseAndCityContainingIgnoreCase("max", "hamburg"))
                .thenReturn(List.of(entity));

        var result = service.getCustomers("max", "hamburg");

        assertThat(result).hasSize(1);
        verify(repo).findByNameContainingIgnoreCaseAndCityContainingIgnoreCase("max", "hamburg");
    }

    @Test
    void getCustomer_existingId_returnsMappedCustomer() {
        when(repo.findById(customerId)).thenReturn(Optional.of(entity));

        Customer result = service.getCustomer(customerId);

        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getName()).isEqualTo("Max Mustermann");
    }

    @Test
    void getCustomer_unknownId_throwsNotFoundException() {
        when(repo.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCustomer(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(customerId.toString());
    }

    @Test
    void createCustomer_savesWithNullId_returnsMappedCustomer() {
        when(repo.save(any(CustomerEntity.class))).thenReturn(entity);
        Customer toCreate = new Customer("Max Mustermann", "Hamburg");
        toCreate.setPassword("hashed-pw");

        Customer result = service.createCustomer(toCreate);

        assertThat(result.getId()).isEqualTo(customerId); // kommt aus dem (gemockten) save()-Ergebnis
        verify(repo).save(argThat(e -> e.getId() == null && e.getName().equals("Max Mustermann")));
    }

    @Test
    void updateCustomer_existingId_savesAndReturnsMappedCustomer() {
        when(repo.existsById(customerId)).thenReturn(true);
        when(repo.save(any(CustomerEntity.class))).thenReturn(entity);
        Customer toUpdate = new Customer("Max Mustermann", "Hamburg");
        toUpdate.setPassword("hashed-pw");

        Customer result = service.updateCustomer(customerId, toUpdate);

        assertThat(result.getId()).isEqualTo(customerId);
        verify(repo).save(argThat(e -> customerId.equals(e.getId())));
    }

    @Test
    void updateCustomer_unknownId_throwsNotFoundException_andNeverSaves() {
        when(repo.existsById(customerId)).thenReturn(false);
        Customer toUpdate = new Customer("Max Mustermann", "Hamburg");

        assertThatThrownBy(() -> service.updateCustomer(customerId, toUpdate))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void deleteCustomer_existingId_deletesSuccessfully() {
        when(repo.existsById(customerId)).thenReturn(true);

        service.deleteCustomer(customerId);

        verify(repo).deleteById(customerId);
    }

    @Test
    void deleteCustomer_unknownId_throwsNotFoundException_andNeverDeletes() {
        when(repo.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteCustomer(customerId))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).deleteById(any());
    }

    @Test
    void getCustomerCount_delegatesToRepository() {
        when(repo.count()).thenReturn(5L);

        assertThat(service.getCustomerCount()).isEqualTo(5L);
    }
}