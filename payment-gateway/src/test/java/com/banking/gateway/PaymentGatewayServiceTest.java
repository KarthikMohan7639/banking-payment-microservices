package com.banking.gateway;

import com.banking.gateway.dto.ChargeRequest;
import com.banking.gateway.dto.ChargeResponse;
import com.banking.gateway.entity.GatewayTransaction;
import com.banking.gateway.entity.TransactionStatus;
import com.banking.gateway.repository.GatewayTransactionRepository;
import com.banking.gateway.service.PaymentGatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PaymentGatewayServiceTest {

    @Autowired
    private PaymentGatewayService service;

    @Autowired
    private GatewayTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testChargeSuccessWithZeroFailureRate() {
        ReflectionTestUtils.setField(service, "failureRate", 0.0);

        ChargeRequest request = ChargeRequest.builder()
                .idempotencyKey("KEY-100")
                .amount(new BigDecimal("250.00"))
                .currency("USD")
                .build();

        ChargeResponse response = service.charge(request);

        assertNotNull(response.getGatewayRef());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
    }

    @Test
    void testIdempotencyReturnsExistingTransaction() {
        ReflectionTestUtils.setField(service, "failureRate", 0.0);

        ChargeRequest request = ChargeRequest.builder()
                .idempotencyKey("KEY-200")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();

        ChargeResponse firstCall = service.charge(request);
        ChargeResponse secondCall = service.charge(request);

        assertEquals(firstCall.getGatewayRef(), secondCall.getGatewayRef());
        assertEquals(firstCall.getStatus(), secondCall.getStatus());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testChargeFailureWithHundredPercentFailureRate() {
        ReflectionTestUtils.setField(service, "failureRate", 1.0);

        ChargeRequest request = ChargeRequest.builder()
                .idempotencyKey("KEY-300")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .build();

        ChargeResponse response = service.charge(request);

        assertNotNull(response.getGatewayRef());
        assertEquals(TransactionStatus.FAILED, response.getStatus());
    }
}
