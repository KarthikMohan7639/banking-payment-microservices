package com.banking.payment.client;

import com.banking.payment.dto.ChargeRequest;
import com.banking.payment.dto.ChargeResponse;
import com.banking.payment.dto.RefundRequest;
import com.banking.payment.dto.RefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign Declarative HTTP Client for communicating with payment-gateway microservice.
 */
// WHY: Creates declarative HTTP client proxy targeting payment-gateway service resolved via Eureka.
@FeignClient(name = "payment-gateway")
public interface PaymentGatewayClient {

    @PostMapping("/gateway/charge")
    ChargeResponse charge(@RequestBody ChargeRequest request);

    @PostMapping("/gateway/refund")
    RefundResponse refund(@RequestBody RefundRequest request);
}
