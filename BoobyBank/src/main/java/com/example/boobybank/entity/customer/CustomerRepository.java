package com.example.boobybank.entity.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Collection<CustomerEntity> findByNameContainingIgnoreCase(String prefix);

    Collection<CustomerEntity> findByCityContainingIgnoreCase(String city);

    Collection<CustomerEntity> findByNameContainingIgnoreCaseAndCityContainingIgnoreCase(String name, String city);

}