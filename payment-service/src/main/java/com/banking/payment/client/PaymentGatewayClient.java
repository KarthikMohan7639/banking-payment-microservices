package com.banking.payment.client;

import com.banking.payment.dto.ChargeRequest;
import com.banking.payment.dto.ChargeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-gateway")
public interface PaymentGatewayClient {

    @PostMapping("/gateway/charge")
    ChargeResponse charge(@RequestBody ChargeRequest request);
}
