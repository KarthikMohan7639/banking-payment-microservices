package com.banking.gateway.service;

import com.banking.gateway.dto.ChargeRequest;
import com.banking.gateway.dto.ChargeResponse;
import com.banking.gateway.entity.GatewayTransaction;
import com.banking.gateway.entity.TransactionStatus;
import com.banking.gateway.repository.GatewayTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final GatewayTransactionRepository repository;

    @Value("${payment.gateway.failure-rate:0.10}")
    private double failureRate;

    public ChargeResponse charge(ChargeRequest request) {
        // 1. Idempotency Check
        Optional<GatewayTransaction> existing = repository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            GatewayTransaction tx = existing.get();
            return new ChargeResponse(tx.getGatewayRef(), tx.getStatus());
        }

        // 2. Simulate PSP Processing (configurable failure rate)
        boolean isSuccess = ThreadLocalRandom.current().nextDouble() >= failureRate;
        TransactionStatus status = isSuccess ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
        String gatewayRef = UUID.randomUUID().toString();

        // 3. Persist and return
        GatewayTransaction tx = GatewayTransaction.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(status)
                .gatewayRef(gatewayRef)
                .build();

        repository.save(tx);
        return new ChargeResponse(gatewayRef, status);
    }

    public ChargeResponse getByGatewayRef(String gatewayRef) {
        GatewayTransaction tx = repository.findByGatewayRef(gatewayRef)
                .orElseThrow(() -> new RuntimeException("Gateway transaction not found for ref: " + gatewayRef));
        return new ChargeResponse(tx.getGatewayRef(), tx.getStatus());
    }
}
