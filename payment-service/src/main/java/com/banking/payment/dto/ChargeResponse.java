package com.banking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeResponse {
    private String gatewayRef;
    private String status; // SUCCESS or FAILED
}
