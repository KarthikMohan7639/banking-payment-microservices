package com.banking.account.repository;

import com.banking.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for managing Account entities.
 */
// WHY: Marks interface as a Spring Data repository bean providing automated CRUD operations.
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByOwnerUsername(String ownerUsername);
}
