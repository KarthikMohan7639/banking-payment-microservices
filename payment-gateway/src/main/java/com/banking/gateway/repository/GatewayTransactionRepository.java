package com.banking.gateway.repository;

import com.banking.gateway.entity.GatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayTransactionRepository extends JpaRepository<GatewayTransaction, Long> {
    Optional<GatewayTransaction> findByIdempotencyKey(String idempotencyKey);
    Optional<GatewayTransaction> findByGatewayRef(String gatewayRef);
}
