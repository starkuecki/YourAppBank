package com.example.boobybank.control.customers;

import com.example.boobybank.control.shared.NotFoundException;
import com.example.boobybank.control.shared.OnWrite;
import com.example.boobybank.entity.customer.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repo;
    private final CustomerMapper mapper;


    @FunctionalInterface
    private interface CustomerFilterStrategy {
        Collection<Customer> apply(String namePrefix, String city);
    }

    private record FilterKey(boolean hasName, boolean hasCity) {
        static FilterKey from(String name, String city) {
            return new FilterKey(name != null, city != null);
        }
    }

    private final Map<FilterKey, CustomerFilterStrategy> filterStrategies = Map.of(
            new FilterKey(true, true), this::getCustomersByNameAndCity,
            new FilterKey(true, false), this::getCustomersByName,
            new FilterKey(false, true), this::getCustomersByCity,
            new FilterKey(false, false), this::getAllCustomers
    );


    public Collection<Customer> getCustomers(String namePrefix, String city) {
        var strategy = filterStrategies.get(FilterKey.from(namePrefix, city));
        return strategy.apply(namePrefix, city);
    }


    public Customer getCustomer(UUID id) {
        return repo.findById(id)
                .map(mapper::map)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " does not exist"));
    }


    @Validated(OnWrite.class)
    public Customer createCustomer(@Valid Customer customer) {
        return mapper.map(repo.save(mapper.map(null, customer)));
    }


    @Validated(OnWrite.class)
    public Customer updateCustomer(UUID id, @Valid Customer customer) {
        requireCustomerExists(id);

        return mapper.map(repo.save(mapper.map(id, customer)));
    }


    public void deleteCustomer(UUID id) {
        requireCustomerExists(id);
        repo.deleteById(id);
    }


    public long getCustomerCount() {
        return repo.count();
    }


    private Collection<Customer> getAllCustomers(String ignoredName, String ignoredCity) {
        return repo.findAll()
                .stream()
                .map(mapper::map)
                .toList();
    }


    private Collection<Customer> getCustomersByName(String name, String ignoredCity) {
        return repo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(mapper::map)
                .toList();
    }


    private Collection<Customer> getCustomersByCity(String ignoredName, String city) {
        return repo.findByCityContainingIgnoreCase(city)
                .stream()
                .map(mapper::map)
                .toList();
    }


    private Collection<Customer> getCustomersByNameAndCity(String name, String city) {
        return repo.findByNameContainingIgnoreCaseAndCityContainingIgnoreCase(name, city)
                .stream()
                .map(mapper::map)
                .toList();
    }


    private void requireCustomerExists(UUID id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Customer with id " + id + " does not exist");
        }
    }

}