package com.banking.gateway.controller;

import com.banking.gateway.dto.ChargeRequest;
import com.banking.gateway.dto.ChargeResponse;
import com.banking.gateway.dto.RefundRequest;
import com.banking.gateway.dto.RefundResponse;
import com.banking.gateway.service.PaymentGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing external PSP charge and refund endpoints.
 */
// WHY: Combines @Controller and @ResponseBody to expose REST JSON endpoints for payment gateway operations.
@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final PaymentGatewayService service;

    @PostMapping("/charge")
    // WHY: Validates input DTO fields (@NotBlank, @Positive) before charge execution.
    public ResponseEntity<ChargeResponse> charge(@Valid @RequestBody ChargeRequest request) {
        return ResponseEntity.ok(service.charge(request));
    }

    @PostMapping("/refund")
    // WHY: Validates refund request DTO before executing SAGA compensating refund.
    public ResponseEntity<RefundResponse> refund(@Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(service.refund(request));
    }

    @GetMapping("/transactions/{gatewayRef}")
    public ResponseEntity<ChargeResponse> getTransaction(@PathVariable String gatewayRef) {
        return ResponseEntity.ok(service.getByGatewayRef(gatewayRef));
    }
}
