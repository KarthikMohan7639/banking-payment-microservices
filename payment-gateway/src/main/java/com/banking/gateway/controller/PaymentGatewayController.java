package com.banking.gateway.controller;

import com.banking.gateway.dto.ChargeRequest;
import com.banking.gateway.dto.ChargeResponse;
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

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final PaymentGatewayService service;

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@Valid @RequestBody ChargeRequest request) {
        return ResponseEntity.ok(service.charge(request));
    }

    @GetMapping("/transactions/{gatewayRef}")
    public ResponseEntity<ChargeResponse> getTransaction(@PathVariable String gatewayRef) {
        return ResponseEntity.ok(service.getByGatewayRef(gatewayRef));
    }
}
