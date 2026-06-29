package com.example.boobybank.entity.accounts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;


public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    Collection<AccountEntity> findByOwnerId(UUID ownerId);


}
